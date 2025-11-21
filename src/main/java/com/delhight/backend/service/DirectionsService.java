package com.delhight.backend.service;

import com.delhight.backend.model.RouteLeg;

/**
 * Abstraction for fetching directions between two coordinates.
 */
public interface DirectionsService {

    /**
     * Query routing provider for one leg between two coordinates.
     */
    RouteLeg getRouteLeg(
            double fromLat,
            double fromLng,
            double toLat,
            double toLng,
            String mode,          // walking / driving / transit
            long departureTime,   // epoch seconds, 0 = omit
            String fromName,      // human readable
            String toName         // human readable
    );
}
