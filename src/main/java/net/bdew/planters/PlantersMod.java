package net.bdew.planters;

import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Communicator;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import net.bdew.planters.actions.*;
import net.bdew.planters.area.BetterFarmHandler;
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
    public static float magicMushroomFavorPerQL = 1f;
    public static float magicMushroomKarmaPerQL = 0.5f;

    @Override
    public void configure(Properties properties) {
        extraHarvest = Integer.parseInt(properties.getProperty("extraHarvest", "3"));
        canWilt = !Boolean.parseBoolean(properties.getProperty("disableWeeds", "true"));
        allowStumpDigging = Boolean.parseBoolean(properties.getProperty("allowStumpDigging", "true"));
        magicMushrooms = allowStumpDigging && Boolean.parseBoolean(properties.getProperty("magicMushrooms", "true"));
        magicUntendedDeathChance = Float.parseFloat(properties.getProperty("magicUntendedDeathChance", "0.2"));
        magicMushroomFavorPerQL = Float.parseFloat(properties.getProperty("magicMushroomFavorPerQL", "1"));
        magicMushroomKarmaPerQL = Float.parseFloat(properties.getProperty("magicMushroomKarmaPerQL", "0.5"));
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
                    .getMethod("run", "()V").insertAfter("net.bdew.planters.Hooks.serverTick();");

            if (allowStumpDigging) {
                classPool.get("com.wurmonline.server.items.Item")
                        .getMethod("getSizeMod", "()F")
                        .insertBefore("if (this.getTemplateId() == net.bdew.planters.MiscItems.stumpId) return net.bdew.planters.MiscItems.stumpSizeMod(this);");
            }

            CtClass ctCommunicator = classPool.getCtClass("com.wurmonline.server.creatures.Communicator");

            ctCommunicator.getMethod("sendItem", "(Lcom/wurmonline/server/items/Item;JZ)V")
                    .insertAfter("net.bdew.planters.Hooks.sendItemHook(this, $1);");

            ctCommunicator.getMethod("sendRemoveItem", "(Lcom/wurmonline/server/items/Item;)V")
                    .insertAfter("net.bdew.planters.Hooks.removeItemHook(this, $1);");

            CtClass ctBehaviourDispatcher = classPool.getCtClass("com.wurmonline.server.behaviours.BehaviourDispatcher");

            ExprEditor topItemRedirect = new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("getBehaviour")) {
                        m.replace("   if (targetType == 10 && net.bdew.planters.Hooks.isPlanterTopItem($1)) {" +
                                "                   target = target - 8;" +
                                "                   targetType = 2;" +
                                "                   $_ = $proceed(target, $2);" +
                                "               } else {" +
                                "                   $_ = $proceed($$);" +
                                "               };");
                    }
                }
            };

            ctBehaviourDispatcher.getMethod("requestActions", "(Lcom/wurmonline/server/creatures/Creature;Lcom/wurmonline/server/creatures/Communicator;BJJ)V")
                    .instrument(topItemRedirect);
            ctBehaviourDispatcher.getMethod("requestSelectionActions", "(Lcom/wurmonline/server/creatures/Creature;Lcom/wurmonline/server/creatures/Communicator;BJJ)V")
                    .instrument(topItemRedirect);
            ctBehaviourDispatcher.getMethod("action", "(Lcom/wurmonline/server/creatures/Creature;Lcom/wurmonline/server/creatures/Communicator;JJS)V")
                    .insertBefore(" if (($4 & 0xFFL) == 10 && net.bdew.planters.Hooks.isPlanterTopItem($4)) $4=$4-8;");

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onServerStarted() {
        try {
            Class.forName("net.bdew.wurm.betterfarm.api.BetterFarmAPI");
            BetterFarmHandler.install();
        } catch (ClassNotFoundException e) {
            logInfo("Better Farm not installed or is too old, skipping integration");
        }

        ModActions.registerActionPerformer(new SowPerformer());
        ModActions.registerActionPerformer(new CultivatePerformer());
        ModActions.registerActionPerformer(new TendPerformer());
        ModActions.registerActionPerformer(new HarvestPerformer());
        ModActions.registerActionPerformer(new DigStumpPerformer());
        ModActions.registerBehaviourProvider(new PlanterBehaviour());

        ModActions.registerActionPerformer(new PreventPlanterPerformer(Actions.LOAD_CARGO));

        ModActions.registerAction(new InfectAction());

        if (magicMushrooms) {
            ModActions.registerActionPerformer(new EatMagicShroomPerformer());
        }

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
