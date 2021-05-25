import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.vecmath.Point3d;

public class sphere {
    public static List<Point3d> points_out = new ArrayList();
    public static List<Float> points_out_values = new ArrayList();

    public static float min_x, max_x;
    public static float min_y, max_y;
    public static float min_z, max_z;
    public static int[] resolutions;

    public static boolean inSphere(Point3d point) {
        Point3d center = new Point3d(25f,25f,25f);
        int radius = 30;
        double is_it = Math.pow((point.x - center.x),2) + Math.pow((point.y - center.y),2) + Math.pow((point.z - center.z),2);
        return is_it < Math.pow(radius, 2);
    }

    public static void main(String[] args) {
        min_y = 0;  //min_vals[1];
        min_z = 0;  //min_vals[2];
        min_x = 0;  //min_vals[0];
        max_x = 50; //max_vals[0];
        max_y = 50; //max_vals[1];
        max_z = 50; //max_vals[2];
        int[] resolutions = {256, 256, 256};

        float step_x = Math.abs(min_x - max_x)/resolutions[0];
        float step_y = Math.abs(min_y - max_y)/resolutions[1];
        float step_z = Math.abs(min_z - max_z)/resolutions[2];

        for (int k=0; k < resolutions[2]; k++) {
            for (int j=0; j < resolutions[1]; j++) {
                for (int i=0; i < resolutions[0]; i++) {
                    Point3d tmp = new Point3d(min_x + i*step_x, min_y + j*step_y, min_z + k*step_z);
                    points_out.add(tmp);
                    if (inSphere(tmp)) {
                        points_out_values.add((float) Math.random());
                    } else {
                        points_out_values.add(0.0f);
                    }
                }
            }
        }
        print_points_out_binary();
    }

    public static void print_points_out_binary() {
        for (Float points_out_value : points_out_values) {
            try {
                float tmp = (float) points_out_value;
                ByteBuffer buff = ByteBuffer.allocate(4).putFloat(tmp);
                System.out.write(buff.array());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
