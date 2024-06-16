package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;

public class Plane extends BBoxedPrimitive {

    private final Point supp;
    private final Vec3 normal;
    Vec3 u, v;

    public Plane(final Point a, final Point b, final Point c) {
        super(BBox.INF);
        this.supp = a;
        this.u = a.sub(b);
        this.v = b.sub(c);
        this.normal = u.cross(v).normalized();
    }

    public Plane(final Vec3 n1, final Point supp1) {
        super(BBox.INF);
        this.normal = n1.normalized();
        this.supp = supp1;

    }

    @Override
    public Hit hitTest(Ray ray, Obj obj, float tmin, float tmax) {
        return new LazyHitTest(obj) {

            private Point point;
            private Vec3 normal_hit;
            private float parameter;
            private float distance;

            @Override
            public float getParameter() {
                return parameter;
            }

            @Override
            public Point getPoint() {
                point = ray.eval(distance);
                return point;
            }

            @Override
            public Vec3 getNormal() {
                normal_hit = normal;
                return normal_hit;
            }

            @Override
            public Vec2 getUV() {
                return Util.computePlaneUV(normal, supp, point);
            }

            @Override
            protected boolean calculateHit() {
                Vec3 vs = ray.dir();
                float denominator = normal.dot(vs);

                if (denominator == 0) {
                    return false;
                }
                distance = supp.dot(normal);
                float psne = ray.base().dot(normal);
                // float distance = ray.base().dot(normal);

                float lambda = (distance - psne) / denominator;

                if (lambda >= Constants.EPS && lambda >= tmin && lambda <= tmax) {
                    point = ray.base().add(vs.scale(lambda));

                    parameter = lambda;
                    return true;
                }

                return false;
            }

        };
    }

    @Override
    public int hashCode() {
        return supp.hashCode() ^ normal.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Plane) {
            final Plane cobj = (Plane) other;
            return cobj.normal.equals(normal) && cobj.supp.equals(supp);
        }
        return false;
    }

}
