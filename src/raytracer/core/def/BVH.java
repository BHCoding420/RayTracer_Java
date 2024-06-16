package raytracer.core.def;

import java.util.ArrayList;
import java.util.List;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.geom.BBox;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec3;

/**
 * Represents a bounding volume hierarchy acceleration structure
 */
public class BVH extends BVHBase {
    // ekhla2 list of objects w bbox
    private BBox bbox;
    private ArrayList<Obj> list;

    public BVH() {

        this.bbox = BBox.EMPTY;
        this.list = new ArrayList<Obj>();

    }

    @Override
    public BBox bbox() {

        return bbox;

    }

    @Override
    public void add(final Obj prim) {

        // Obj o =prim;
        if (prim != null) {
            list.add(prim);
            bbox = BBox.surround(prim.bbox(), bbox);
        }

    }

    /**
     * Builds the actual bounding volume hierarchy
     */
    @Override
    public void buildBVH() {

        if (list.size() <= 4) {
            return;
        }

        BVHBase a = new BVH();
        BVHBase b = new BVH();

        int spliT_dim = calculateSplitDimension(bbox.getMin().sub(calculateMaxOfMinPoints()));
        float mid = (bbox.getMin().get(spliT_dim) + bbox.getMax().get(spliT_dim)) / 2;
        distributeObjects(a, b, spliT_dim, mid);
        a.buildBVH();
        b.buildBVH();

        // throw new UnsupportedOperationException("This method has not yet been
        // implemented.");
    }

    @Override
    public Point calculateMaxOfMinPoints() {

        Point max_pt = list.get(0).bbox().getMin();

        for (Obj obj : list) {

            max_pt = max_pt.max(obj.bbox().getMin());
        }

        return max_pt;

    }

    @Override
    public int calculateSplitDimension(final Vec3 extent) {

        // ma3tine extent 3al x,y,z,iza x > y > z b3mal split 3al x-axis(0)
        if (extent.get(0) >= extent.get(1) && extent.get(0) >= extent.get(2)) {
            return 0;
        } else if (extent.get(1) >= extent.get(0) && extent.get(1) >= extent.get(2)) {
            return 1;
        } else {
            return 2;
        }

    }

    @Override
    public void distributeObjects(final BVHBase a, final BVHBase b,
            final int splitDim, final float splitPos) {

        for (Obj o : list) {

            if (o.bbox().getMin().get(splitDim) < splitPos) {
                a.add(o);
            } else {
                b.add(o);
            }

        }

        // throw new UnsupportedOperationException("This method has not yet been
        // implemented.");
    }

    @Override
    public Hit hit(final Ray ray, final Obj obj, final float tMin, final float tMax) {
        Hit nearest = Hit.No.get();
        if (bbox.hit(ray, tMin, tMax).hits()) {
            float tmax_temp = tMax;

            for (final Obj p : list) {
                final Hit hit = p.hit(ray, p, tMin, tMax);
                if (hit.hits()) {
                    final float t = hit.getParameter();
                    if (t < tmax_temp) {
                        nearest = hit;
                        tmax_temp = t;
                    }
                }
            }
        }
        return nearest;
    }

    @Override
    public List<Obj> getObjects() {
        return list;

    }
}
