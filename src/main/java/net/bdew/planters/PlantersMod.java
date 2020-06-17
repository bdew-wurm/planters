package net.bdew.planters;

import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Communicator;
import javassist.ClassPool;
import javassist.CtClass;
import net.bdew.planters.actions.*;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlantersMod implements WurmServerMod, Initable, PreInitable, Configurable, ServerStartedListener, ItemTemplatesCreatedListener, PlayerMessageListener {
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
    public static int extraHarvest = 0;
    public static boolean allowStumpDigging = true;
    public static boolean magicMushrooms = true;
    public static float magicUntendedDeathChance = 0.5f;

    @Override
    public void configure(Properties properties) {
        extraHarvest = Integer.parseInt(properties.getProperty("extraHarvest", "3"));
        canWilt = !Boolean.parseBoolean(properties.getProperty("disableWeeds", "true"));
        allowStumpDigging = Boolean.parseBoolean(properties.getProperty("allowStumpDigging", "true"));
        magicMushrooms = allowStumpDigging && Boolean.parseBoolean(properties.getProperty("magicMushrooms", "true"));
        magicUntendedDeathChance = Float.parseFloat(properties.getProperty("magicUntendedDeathChance", "0.2"));
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

            classPool.get("com.wurmonline.server.items.Item")
                    .getMethod("getSizeMod", "()F")
                    .insertBefore("if (this.getTemplateId() == net.bdew.planters.MiscItems.stumpId) return net.bdew.planters.MiscItems.stumpSizeMod(this);");

            CtClass ctCommunicator = classPool.getCtClass("com.wurmonline.server.creatures.Communicator");

            ctCommunicator.getMethod("sendItem", "(Lcom/wurmonline/server/items/Item;JZ)V")
                    .insertAfter("net.bdew.planters.Hooks.sendItemHook(this, $1);");

            ctCommunicator.getMethod("sendRemoveItem", "(Lcom/wurmonline/server/items/Item;)V")
                    .insertAfter("net.bdew.planters.Hooks.removeItemHook(this, $1);");


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
        ModActions.registerActionPerformer(new DigStumpPerformer());
        ModActions.registerBehaviourProvider(new PlanterBehaviour());

        ModActions.registerActionPerformer(new PreventPlanterPerformer(Actions.LOAD_CARGO));

        logInfo(String.format("Loaded %d planters that need polling", PlanterTracker.trackedCount()));
    }

    @Override
    public void onItemTemplatesCreated() {
        try {
            MiscItems.register();
            PlanterItem.register();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onPlayerMessage(Communicator communicator, String s) {
        return false;
    }

    @Override
    public MessagePolicy onPlayerMessage(Communicator communicator, String message, String title) {
        if (message.startsWith("#") && communicator.getPlayer().getPower() == 5)
            return GmCommands.handle(communicator, message, title);
        return MessagePolicy.PASS;
    }
}
