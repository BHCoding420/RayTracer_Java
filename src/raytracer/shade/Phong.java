package raytracer.shade;

import java.util.Collection;

import raytracer.core.Hit;
import raytracer.core.LightSource;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Point;
import raytracer.math.Vec3;

public class Phong implements Shader {

    private Shader inner;
    private Color ambient;
    private float diffuse;
    private float specular;
    private float shininess;

    public Phong(final Shader inner, final Color ambient, final float diffuse, final float specular,
            final float shininess) {
        this.inner = inner;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    @Override
    public Color shade(Hit hit, Trace trace) {
        Color Iambient = ambient;

        Color Csub = inner.shade(hit, trace);

        Collection<LightSource> lightsources = trace.getScene().getLightSources();

        Color I_diffuse = Color.BLACK;
        Color I_specular = Color.BLACK;

        for (LightSource src : lightsources) {
            Color src_color = src.getColor(); // get color form light source
            Vec3 v = src.getLocation().sub(hit.getPoint()); // vector from hitpoint to light source
            Vec3 normalised_v = v.normalized();
            Trace ray_shadow = trace.spawn(hit.getPoint(), normalised_v);

            boolean valid_src = true;

            /*
             * if (ray_shadow.getHit().hits()) {
             * Point h = ray_shadow.getHit().getPoint();
             * Vec3 z = h.sub(src.getLocation()); // hitpoint from shadow minus light
             * 
             * if (z.sdot() < v.sdot()) {
             * valid_src = true;
             * }
             * // sdot(shadow)
             * else {
             * continue;
             * }
             * 
             * } else if (!ray_shadow.getHit().hits()) {
             * // valid_src = false;
             * System.out.println("Ray does not hit");
             * 
             * // continue;
             * }
             * 
             * System.out.println(valid_src);
             */

            Vec3 normal = hit.getNormal();
            // Vec3 v = src.getLocation().sub(hit.getPoint());

            if (valid_src) {
                Color c = src_color.mul(Csub).scale(diffuse).scale(Math.max(0, normalised_v.dot(normal)));

                I_diffuse = I_diffuse.add(c);

                Vec3 d = trace.getRay().dir();

                float max_thingy = Math.max(0, d.reflect(normal).dot(normalised_v));
                c = src_color.scale(specular).scale((float) Math.pow(max_thingy, shininess));

                I_specular = I_specular.add(c);
            }
        }

        Color I_phong = Iambient.add(I_diffuse).add(I_specular);

        return I_phong;
    }
}
