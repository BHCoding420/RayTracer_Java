package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;
import java.lang.Math;

public class Sphere extends BBoxedPrimitive {

    private final Point m;
    private final float r;

    public Sphere(final Point mid, final float radius) {

        // Point p1 = mid.add(new Vec3(r, 0, 0));
        // Point p2 = mid.add(new Vec3(-r, 0, 0));

        super(BBox.create(mid.add(new Vec3(radius, radius, radius)), mid.sub(new Vec3(radius, radius, radius))));

        this.m = mid;
        this.r = radius;

    }

    @Override
    public Hit hitTest(Ray ray, Obj obj, float tmin, float tmax) {

        return new LazyHitTest(obj) {
            private Point point;
            private float r1;
            private Vec3 normal;

            @Override
            public float getParameter() {
                return r1; // should be center of sphere to the point of contact?
            }

            @Override
            public Point getPoint() {
                if (point == null)
                    point = ray.eval(r1);
                return point;
            }

            @Override
            public Vec3 getNormal() {
                normal = ray.eval(r1).sub(m).normalized();
                return normal;
            }

            @Override
            public Vec2 getUV() {
                Vec3 res = point.sub(m);

                return Util.computeSphereUV(res);
            }

            @Override
            protected boolean calculateHit() {
                Point ps = ray.base();
                Vec3 ps_minus_ck = ps.sub(m);
                float b = 2.0f * ray.dir().dot(ps_minus_ck);
                float c = ps_minus_ck.dot(ps_minus_ck) - r * r;
                System.out.println("b : " + b);
                System.out.println("c : " + c);

                float discriminate = (b * b) - 4.0f * c;
                System.out.println("discrim : " + discriminate);

                if (discriminate >= 0) {
                    float sqrtDiscriminant = (float) Math.sqrt(discriminate);
                    float lambda1 = (-b - sqrtDiscriminant) / 2.0f;
                    float lambda2 = (-b + sqrtDiscriminant) / 2.0f;

                    System.out.println("lambda1 : " + lambda1);
                    System.out.println("lambda2 : " + lambda2);
                    float lambda;

                    // take the min,and then do the checks
                    lambda = Float.min(lambda1, lambda2);
                    if (lambda < tmin || lambda > tmax || lambda < Constants.EPS) {
                        return false;
                    }
                    r1 = (float) lambda;
                    // System.out.println(lambda);

                    point = ray.eval(r1);
                    normal = getNormal();

                    return true;

                }

                return false;
            }

            /*
             * @Override
             * protected boolean calculateHit() {
             * Vec3 oc = ray.base().sub(m);
             * float a = ray.dir().dot(ray.dir());
             * float b = 2 * ray.dir().dot(oc);
             * float c = oc.dot(oc) - r * r;
             * float discriminant = b * b - 4 * c;
             * 
             * if (discriminant >= 0) {
             * float sqrtDiscriminant = (float) Math.sqrt(discriminant);
             * 
             * // Calculate the solutions for lambda
             * float lambda1 = (-b - sqrtDiscriminant) / (2 * a);
             * float lambda2 = (-b + sqrtDiscriminant) / (2 * a);
             * 
             * // Choose the smaller non-negative solution
             * float lambda = Math.min(lambda1, lambda2);
             * 
             * // Check if the solution is within the acceptable range
             * if (lambda >= tmin && lambda <= tmax) {
             * r1 = lambda;
             * return true;
             * }
             * }
             * 
             * return false;
             * }
             */

        };

    }

    @Override
    public int hashCode() {
        return m.hashCode() ^ Float.hashCode(r);
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Sphere) {
            final Sphere cobj = (Sphere) other;
            return cobj.m.equals(m) && cobj.r == r;
        }
        return false;
    }

}
