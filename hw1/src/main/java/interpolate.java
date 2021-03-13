import cn.jimmiez.pcu.common.graphics.Octree;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.vecmath.Point3d;

class shepard_method {
    public static List<Point3d> points = new ArrayList();
    public static List<Double> point_values = new ArrayList();

    public static List<Point3d> points_out = new ArrayList();
    public static List<Double> points_out_values = new ArrayList();

    public static float min_x, max_x;
    public static float min_y, max_y;
    public static float min_z, max_z;
    public static int[] resolutions;


    public shepard_method(float[] min_vals, float[] max_vals, int[] in_resolutions){
        min_x = min_vals[0];
        min_y = min_vals[1];
        min_z = min_vals[2];
        max_x = max_vals[0];
        max_y = max_vals[1];
        max_z = max_vals[2];
        resolutions = in_resolutions;

        double step_x = Math.abs(min_x - max_x)/resolutions[0];
        double step_y = Math.abs(min_y - max_y)/resolutions[1];
        double step_z = Math.abs(min_z - max_z)/resolutions[2];

        /*
        for (double k=min_z; k<max_z; k+=step_z) {
            for (double j=min_y; j<max_y; j+=step_y) {
                for (double i=min_x; i<max_x; i+=step_x) {
                    points_out.add(new Point3d(i, j, k));
                    points_out_values.add(0.0);
                }
            }
        }
        */
        for (int k=0; k < resolutions[2]; k++) {
            for (int j=0; j < resolutions[1]; j++) {
                for (int i=0; i < resolutions[0]; i++) {
                    Point3d tmp = new Point3d(min_x + i*step_x, min_y + j*step_y, min_z + k*step_z);
                    points_out.add(tmp);
                    points_out_values.add(0.0);
                    //System.out.printf("%d, %d, %d\n", i,j,k);
                }
            }
        }
        //System.out.printf("NUM: %d\n", points_out_values.size());
    }

    public void read_from_file(String file_path) {
        String[] data;
        points = new ArrayList<Point3d>();
        point_values = new ArrayList<Double>();
        try {
            File myObj = new File(file_path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data = myReader.nextLine().split(" ");
                points.add(new Point3d(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2])));
                point_values.add(Double.parseDouble(data[3]));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void add_point(Point3d point, Double point_value) {
        points.add(point);
        point_values.add(point_value);
    }

    private static Double weights_basic(int x, int x_k, int p) {
        Double distance = points_out.get(x).distance(points.get(x_k));
        return 1/Math.pow(distance, p);
    }

    public void basic_shepard_method(int p){
        for (int i=0; i<points_out.size(); i++) {
            Double f_x_up = 0.0;
            Double f_x_down = 0.0;
            for (int j=0; j<points.size(); j++) {
                if (points_out.get(i).distance(points.get(j)) == 0) {
                    f_x_up = point_values.get(j);
                    f_x_down = 1.0;
                    break;
                }
                f_x_up += weights_basic(i, j, p) * point_values.get(j);
                f_x_down += weights_basic(i, j, p);
            }
            points_out_values.set(i, f_x_up / f_x_down);
        }
    }

    private static Double weights_modified(int x, int x_k, Double R) {
        Double distance = points_out.get(x).distance(points.get(x_k));
        Double up = Math.max(0, R - distance);
        Double down = R * distance;
        return Math.pow(up/down, 2);
    }

    public void modified_shepard_method(float R){
        Octree octree = new Octree();
        octree.buildIndex(points);

        for (int i=0; i<points_out.size(); i++) {
            Double f_x_up = 0.0;
            Double f_x_down = 0.0;
            try {
                List<Integer> found = octree.searchAllNeighborsWithinDistance(points_out.get(i), R);
                for (int j_index=0; j_index<found.size(); j_index++) {
                    int j = found.get(j_index);
                    if (points_out.get(i).distance(points.get(j)) == 0) {
                        f_x_up = point_values.get(j);
                        f_x_down = 1.0;
                        break;
                    }
                    f_x_up += weights_modified(i, j, (double) R) * point_values.get(j);
                    f_x_down += weights_modified(i, j, (double) R);
                }
                points_out_values.set(i, f_x_up / f_x_down);

            } catch (Exception e) {
                int tmp_point_indx = octree.searchNearestNeighbors(1, points_out.get(i))[0];
                points_out_values.set(i, point_values.get(tmp_point_indx));
            }

        }
    }

    public void print_points() {
        for (int i=0; i<points.size(); i++) {
            System.out.print(points.get(i));
            System.out.print(" : ");
            System.out.println(point_values.get(i));
        }
    }

    public void print_points_out() {
        for (int i=0; i<points_out.size(); i++) {
            System.out.print(points_out.get(i));
            System.out.print(" : ");
            System.out.println(points_out_values.get(i));
        }
        System.out.println(points_out.size());
    }

    public void print_points_out_binary() {
        for (Double points_out_value : points_out_values) {
            try {
                float tmp = (float) (double) points_out_value;
                ByteBuffer buff = ByteBuffer.allocate(4).putFloat(tmp);
                System.out.write(buff.array());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


public class interpolate {
    public static String method;
    public static float min_x;
    public static float max_x;
    public static float min_y;
    public static float max_y;
    public static float min_z;
    public static float max_z;
    public static int res_x;
    public static int res_y;
    public static int res_z;
    public static float R;
    public static int p;

    public static void parse_args(String[] args) {
        for (int i=0; i<args.length; i++) {
            switch (args[i]) {
                case "--method": i++;
                    method = args[i];
                    break;
                case "--min-x": i++;
                    min_x = Float.parseFloat(args[i]);
                    break;
                case "--max-x": i++;
                    max_x = Float.parseFloat(args[i]);
                    break;
                case "--min-y": i++;
                    min_y = Float.parseFloat(args[i]);
                    break;
                case "--max-y": i++;
                    max_y = Float.parseFloat(args[i]);
                    break;
                case "--min-z": i++;
                    min_z = Float.parseFloat(args[i]);
                    break;
                case "--max-z": i++;
                    max_z = Float.parseFloat(args[i]);
                    break;
                case "--res-x": i++;
                    res_x = Integer.parseInt(args[i]);
                    break;
                case "--res-y": i++;
                    res_y = Integer.parseInt(args[i]);
                    break;
                case "--res-z": i++;
                    res_z = Integer.parseInt(args[i]);
                    break;
                case "--p": i++;
                    p = Integer.parseInt(args[i]);
                    break;
                case "--r": i++;
                    R = Float.parseFloat(args[i]);
                    break;
                default: System.out.printf("Unknown argument: %s %s\n", args[i], args[i+1]);
                    i++;
                    break;
            }
        }
    }

    public static void main(String[] args) {
        parse_args(args);

        float[] min_vals = {min_x, min_y, min_z};
        float[] max_vals = {max_x, max_y, max_z};
        int[] resolution = {res_x, res_y, res_z};

        //float[] min_vals = {-1f, -1f, -1f};
        //float[] max_vals = {1f, 1f, 1f};
        //int[] resolution = {10, 10, 10};

        shepard_method sh_m = new shepard_method(min_vals, max_vals, resolution);

        Scanner scnr = new Scanner(new InputStreamReader(System.in));

        //Reading each line of file using Scanner class
        while(scnr.hasNextLine()){
            String[] point_data = scnr.nextLine().split(" ");

            Point3d tmp_point = new Point3d(Double.parseDouble(point_data[0]), Double.parseDouble(point_data[1]), Double.parseDouble(point_data[2]));
            Double tmp_point_value = Double.parseDouble(point_data[3]);
            sh_m.add_point(tmp_point, tmp_point_value);
        }

        if (method.equals("basic")) {
            sh_m.basic_shepard_method(p);
        } else if (method.equals("modified")) {
            sh_m.modified_shepard_method(R);
        } else {
            System.out.printf("Unknown method: %s\n", method);
        }

        sh_m.print_points_out_binary();
    }

}

