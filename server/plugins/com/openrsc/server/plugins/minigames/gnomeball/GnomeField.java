package com.openrsc.server.plugins.minigames.gnomeball;


import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;


public class GnomeField {
    private final Point GNOME_GOAL = new Point(729, 450);

    private final Point LOW_FIELD = new Point(742, 450);

    private static GnomeField instance;

    private GnomeField() {
    }

    public static GnomeField getInstance() {
        if (GnomeField.instance == null) {
            GnomeField.instance = new GnomeField();
        }
        return GnomeField.instance;
    }

    // see gnome field map
    // since the field is odd, takes into account unwalkable pos to simplify when possible
    public GnomeField.Zone resolvePositionToZone(Player p) {
        int distanceXToGoal = p.getX() - GNOME_GOAL.getX();
        int distanceYToGoal = p.getY() - GNOME_GOAL.getY();
        double skewedYdistToGoal = distanceYToGoal + 0.5;
        // the central Y-ref of low field is the same as the gnome goal
        // so additionally only the x distance to low field is calculated
        int distanceXToLowField = p.getX() - LOW_FIELD.getX();
        // zone of 2XP (inner)
        if (((Math.abs(distanceYToGoal) <= 2) && (distanceXToGoal >= 0)) && (distanceXToGoal <= 2)) {
            return GnomeField.Zone.ZONE_2XP_INNER;
        } else// zone of 2XP (outer)

            if (((Math.abs(skewedYdistToGoal) < 4) && (distanceXToGoal >= 5)) && (distanceXToGoal <= 8)) {
                return GnomeField.Zone.ZONE_2XP_OUTER;
            } else// zone of 1XP (intercepts of zone 2 checked before)
            // so these checks are safe now
            // (inner)

                if (((Math.abs(skewedYdistToGoal) < 6) && (distanceXToGoal >= 1)) && (distanceXToGoal <= 4)) {
                    return GnomeField.Zone.ZONE_1XP_INNER;
                } else// (outer)

                    if (((Math.abs(skewedYdistToGoal) < 6) && (distanceXToGoal >= 9)) && (distanceXToGoal <= 12)) {
                        return GnomeField.Zone.ZONE_1XP_OUTER;
                    } else// zone of passing the ball
                    // pyramid-like from low field

                        if ((((p.getX() == LOW_FIELD.getX()) && (distanceYToGoal >= (-5))) && (distanceYToGoal <= (-1))) || ((p.getX() > LOW_FIELD.getX()) && (discreteDistance(new Point(LOW_FIELD.getX() + 1, LOW_FIELD.getY()), new Point(p.getX(), p.getY()), true, true) <= 3))) {
                            return GnomeField.Zone.ZONE_PASS;
                        } else// zone of no pass (intercepts of zone of passes checked earlier)

                            if ((((Math.abs(skewedYdistToGoal) < 6) && (distanceXToLowField >= 1)) && (distanceXToLowField <= 5)) || ((distanceXToLowField == 6) && (Math.abs(skewedYdistToGoal) < 5))) {
                                return GnomeField.Zone.ZONE_NO_PASS;
                            } else// no visibility zone

                                if ((((((p.getX() == LOW_FIELD.getX()) && (distanceYToGoal >= 0)) && (distanceYToGoal <= 4)) || (((distanceXToLowField >= (-2)) && (distanceXToLowField <= (-1))) && (((int) (Math.abs(skewedYdistToGoal))) == 6))) || (((Math.abs(skewedYdistToGoal) < 5) && (distanceXToGoal >= (-1))) && (distanceXToLowField <= 0))) || ((distanceXToLowField == 2) && (Math.abs(skewedYdistToGoal) < 4))) {
                                    return GnomeField.Zone.ZONE_NOT_VISIBLE;
                                } else// outside but throwable

                                    if (((p.getX() >= 720) && (p.getX() <= 743)) && ((p.getY() >= 440) && (p.getY() <= 463))) {
                                        return GnomeField.Zone.ZONE_OUTSIDE_THROWABLE;
                                    }







        // outside but non-throwable (kept by player)
        return GnomeField.Zone.ZONE_OUTSIDE_KEEP;
    }

    private int discreteDistance(Point base, Point p, boolean skewX, boolean skewY) {
        double offsetX = (skewX) ? 0.5 : 0.0;
        double offsetY = (skewY) ? 0.5 : 0.0;
        int distanceX = ((int) (Math.abs((p.getX() - base.getX()) + offsetX)));// skew to right

        int distanceY = ((int) (Math.abs((p.getY() - base.getY()) + offsetY)));// skew to up

        return distanceX + distanceY;
    }

    enum Zone {

        ZONE_1XP_INNER,
        ZONE_1XP_OUTER,
        ZONE_2XP_INNER,
        ZONE_2XP_OUTER,
        ZONE_PASS,
        ZONE_NO_PASS,
        ZONE_NOT_VISIBLE,
        ZONE_OUTSIDE_THROWABLE,
        ZONE_OUTSIDE_KEEP;}
}

