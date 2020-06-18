package net.bdew.planters;

import com.wurmonline.communication.SocketConnection;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Optional;

public class MushroomEatEffect implements TickingPlayerEffect {
    private final float favorPerTick;
    private final int tickInterval;
    private int tickCount;
    private boolean didSpecial = false;

    public MushroomEatEffect(float favorPerTick, int tickCount, int tickInterval) {
        this.favorPerTick = favorPerTick;
        this.tickCount = tickCount;
        this.tickInterval = tickInterval;
    }

    private void specialEffect(Player player) {
        float randEffect = Server.rand.nextFloat();
        if (randEffect < 0.10f) {
            Server.getInstance().broadCastAction(String.format("%s starts giggling.", player.getName()), player, 5);
            try {
                SocketConnection con = player.getCommunicator().getConnection();
                final ByteBuffer bb = con.getBuffer();
                bb.put((byte) 56);
                con.flush();
            } catch (IOException e) {
                PlantersMod.logException("Error sending rarity effect", e);
            }
            player.playPersonalSound(player.getSex() == 0 ? "sound.emote.chuckle.male" : "sound.emote.chuckle.female");
            didSpecial = true;
        } else if (randEffect < 0.20f) {
            Server.getInstance().broadCastAction(String.format("%s looks around in terror.", player.getName()), player, 5);
            try {
                ReflectionUtil.callPrivateMethod(player, ReflectionUtil.getMethod(Player.class, "sendNewPhantasm", new Class[]{Boolean.TYPE}), true);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                PlantersMod.logException("Error sending phantasm", e);
            }
            player.playPersonalSound("sound.death.dragon");
            didSpecial = true;
        } else if (randEffect < 0.30f && player.isOnSurface()) {
            Server.getInstance().broadCastAction(String.format("%s grows pale.", player.getName()), player, 5);
            int tile = Server.surfaceMesh.getTile(player.getTileX(), player.getTileY());
            float height = Math.max(0.0f, Tiles.decodeHeightAsFloat(tile));
            player.getCommunicator().sendAddEffect(WurmId.getNextIllusionId(), (short) 1, player.getPosX(), player.getPosY(), height, (byte) 0);
            didSpecial = true;
        } else if (randEffect < 0.40f) {
            Server.getInstance().broadCastAction(String.format("%s screams in pain.", player.getName()), player, 5);
            player.getCommunicator().sendAddEffect(WurmId.getNextIllusionId(), player.getWurmId(), (short) 27, player.getPosX(), player.getPosY(), player.getPositionZ(), (byte) player.getLayer(), "karmaFireball", 10f, 0f);
            player.playPersonalSound("sound.magicTurret.attack");
            didSpecial = true;
        }
    }

    @Override
    public Optional<Integer> apply(Player player) {
        if (!didSpecial) specialEffect(player);
        if (favorPerTick > 0) {
            try {
                player.setFavor(player.getFavor() + favorPerTick);
            } catch (IOException e) {
                PlantersMod.logException("Error setting favor", e);
            }
        }
        tickCount -= 1;
        if (tickCount > 0 && (favorPerTick > 0 || !didSpecial))
            return Optional.of(tickInterval);
        else
            return Optional.empty();
    }
}
