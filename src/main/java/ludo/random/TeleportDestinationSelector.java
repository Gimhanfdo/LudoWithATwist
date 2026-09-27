package ludo.random;

import ludo.domain.enums.TeleportDestination;

public interface TeleportDestinationSelector {

    TeleportDestination selectDestination();
}