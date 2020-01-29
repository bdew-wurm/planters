package net.bdew.planters;

import net.bdew.planters.actions.CultivatePerformer;
import net.bdew.planters.actions.PlanterBehaviour;
import net.bdew.planters.actions.SowPerformer;
import net.bdew.planters.actions.TendPerformer;
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

    @Override
    public void configure(Properties properties) {
    }

    @Override
    public void init() {
    }

    @Override
    public void preInit() {
        ModActions.init();
    }

    @Override
    public void onServerStarted() {
        ModActions.registerActionPerformer(new SowPerformer());
        ModActions.registerActionPerformer(new CultivatePerformer());
        ModActions.registerActionPerformer(new TendPerformer());
        ModActions.registerBehaviourProvider(new PlanterBehaviour());
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
