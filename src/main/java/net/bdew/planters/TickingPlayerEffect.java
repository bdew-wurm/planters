package net.bdew.planters;

import com.wurmonline.server.players.Player;

import java.util.Optional;
import java.util.function.Function;

@FunctionalInterface
public interface TickingPlayerEffect extends Function<Player, Optional<Integer>> {

}
