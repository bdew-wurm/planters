package net.bdew.planters;

import javassist.ClassPool;
import net.bdew.planters.actions.*;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlantersMod implements WurmServerMod, Initable, PreInitable, Configurable, ServerStartedListener, ItemTemplatesCreatedListener {
    private static final Logger logger = Logger.getLogger("PlantersMod");

    public static void logException(String msg, Throwable e) {
        if (logger != null)
            logger.log(Level.SEVERE, msg, e);
    }

    public static void logWarning(String msg) {
        if (logger != null)
            logger.log(Level.WARNING, msg);
    }

    public static void logInfo(String msg) {
        if (logger != null)
            logger.log(Level.INFO, msg);
    }

    public static boolean canWilt = true;

    @Override
    public void configure(Properties properties) {
    }

    @Override
    public void init() {
    }

    @Override
    public void preInit() {
        try {
            ModActions.init();

            ClassPool classPool = HookManager.getInstance().getClassPool();

            classPool.getCtClass("com.wurmonline.server.zones.Zone").getMethod("addItem", "(Lcom/wurmonline/server/items/Item;ZZZ)V")
                    .insertAfter("if ($4) net.bdew.planters.Hooks.addItemLoading($1);");

            classPool.getCtClass("com.wurmonline.server.Server")
                    .getMethod("run", "()V").insertAfter("net.bdew.planters.Hooks.pollPlanters();");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onServerStarted() {
        ModActions.registerActionPerformer(new SowPerformer());
        ModActions.registerActionPerformer(new CultivatePerformer());
        ModActions.registerActionPerformer(new TendPerformer());
        ModActions.registerActionPerformer(new HarvestPerformer());
        ModActions.registerBehaviourProvider(new PlanterBehaviour());

        logInfo(String.format("Loaded %d planters that need polling", PlanterTracker.trackedCount()));
    }

    @Override
    public void onItemTemplatesCreated() {
        try {
            PlanterItem.register();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
