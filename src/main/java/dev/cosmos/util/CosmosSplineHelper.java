package dev.cosmos.util;

import net.minecraft.world.phys.Vec3;

public class CosmosSplineHelper {

    //information on catmull-rom interpolation: https://en.wikipedia.org/wiki/Catmull%E2%80%93Rom_spline

    /**
     * Performs Catmull-Rom interpolation between p1 and p2.
     * @param p0 Control point before p1
     * @param p1 Start of the segment
     * @param p2 End of the segment
     * @param p3 Control point after p2
     * @param t Interpolation factor (0.0 to 1.0)
     */
    public static Vec3 catmullRom(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, double t) {
        double t2 = t * t;
        double t3 = t2 * t;

        double x = 0.5 * ((2 * p1.x) + (-p0.x + p2.x) * t +
                (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * t2 +
                (-p0.x + 3 * p1.x - 3 * p2.x + p3.x) * t3);

        double y = 0.5 * ((2 * p1.y) + (-p0.y + p2.y) * t +
                (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * t2 +
                (-p0.y + 3 * p1.y - 3 * p2.y + p3.y) * t3);

        double z = 0.5 * ((2 * p1.z) + (-p0.z + p2.z) * t +
                (2 * p0.z - 5 * p1.z + 4 * p2.z - p3.z) * t2 +
                (-p0.z + 3 * p1.z - 3 * p2.z + p3.z) * t3);

        return new Vec3(x, y, z);
    }
}