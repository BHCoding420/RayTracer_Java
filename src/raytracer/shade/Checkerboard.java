package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;

public class Checkerboard implements Shader {

    private Shader a, b;
    private float scale;

    public Checkerboard(Shader a, Shader b, float scale) {
        this.a = a;
        this.b = b;
        this.scale = scale;
    }

    @Override
    public Color shade(Hit hit, Trace trace) {

        float u = hit.getUV().x();
        float v = hit.getUV().y();

        float x = (float) (Math.floor(u / scale) + Math.floor(v / scale));

        if (x % 2 == 0) {
            return a.shade(hit, trace);
        } else {
            return b.shade(hit, trace);
        }

    }
}
