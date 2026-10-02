package ludo.random;

import ludo.domain.model.GameAction;

import java.util.List;

public interface ActionSelector {

    GameAction select(
            List<GameAction> actions
    );
}