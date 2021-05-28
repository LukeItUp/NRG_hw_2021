import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.vecmath.Point3d;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;


public class animate {
    private static int frameRate;
    private static int startTimeRange;
    private static int endTimeRange;
    //private static ArrayList<String> kfFiles = new ArrayList<String>();
    //private static ArrayList<Integer> kfTimestamps = new ArrayList<Integer>();
    private static ArrayList<ObjFile> kfObjFiles = new ArrayList<ObjFile>();
    private static ArrayList<ObjFile> outObjFiles = new ArrayList<ObjFile>();

    public static Point3d CatmullRom(Point3d p0, Point3d p1, Point3d p2, Point3d p3, float t, float u) {
        double[][] points = { {p0.x,p1.x,p2.x,p3.x}, {p0.y,p1.y,p2.y,p3.y}, {p0.z,p1.z,p2.z,p3.z}};
        RealMatrix mPoints = MatrixUtils.createRealMatrix(points);
        double [][] tensions = {{-t, 2*t, -t, 0}, {2-t, t-3, 0, 1}, {t-2, 3-2*t, t, 0}, {t, -t, 0, 0}};
        RealMatrix mTensions = MatrixUtils.createRealMatrix(tensions);
        double [][] us = {{Math.pow(u,3)}, {Math.pow(u,2)}, {u}, {1}};
        RealMatrix mUs = MatrixUtils.createRealMatrix(us);

        RealMatrix mPoint = mPoints.multiply(mTensions).multiply(mUs);
        double[] rPoint = mPoint.getColumn(0);
        return new Point3d(rPoint[0], rPoint[1], rPoint[2]);
    }

    public static void ReadKeyFramesFile(String file_path) {
        try {
            File myObj = new File(file_path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(" ");
                //kfFiles.add(data[0]);
                //kfTimestamps.add(Integer.parseInt(data[1]));
                ObjFile tmp = new ObjFile("./data/" + data[0]);
                tmp.keyFrame = Integer.parseInt(data[1]);
                kfObjFiles.add(tmp);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void generateFrames2() {
        /*
        int n_kf = kfObjFiles.size();
        int n_vertices = kfObjFiles.get(0).vertices.size();
        for (int kf_i=0; kf_i < n_kf; kf_i++) {
            System.out.println(kf_i);
            int n_of_frames = Math.abs((kfObjFiles.get(kf_i+1).keyFrame - kfObjFiles.get(kf_i).keyFrame)/frameRate);
            for (int u=0; u <= n_of_frames; u++) {
                ObjFile tmp = new ObjFile(kfObjFiles.get(kf_i));
                tmp.vertices.clear();

                if (kf_i == 0) {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d p = CatmullRom(kfObjFiles.get(kf_i).vertices.get(v),
                                kfObjFiles.get(kf_i).vertices.get(v),
                                kfObjFiles.get(kf_i+1).vertices.get(v),
                                kfObjFiles.get(kf_i+2).vertices.get(v), 0.5f, (float)u/n_of_frames);
                        tmp.vertices.add(p);
                    }
                } else if (kf_i == n_kf - 2) {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d p = CatmullRom(kfObjFiles.get(kf_i-1).vertices.get(v),
                                kfObjFiles.get(kf_i).vertices.get(v),
                                kfObjFiles.get(kf_i+1).vertices.get(v),
                                kfObjFiles.get(kf_i+1).vertices.get(v), 0.5f, (float)u/n_of_frames);
                        tmp.vertices.add(p);
                    }

                } else if (kf_i == n_kf - 1) {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d p = CatmullRom(kfObjFiles.get(kf_i-1).vertices.get(v),
                                kfObjFiles.get(kf_i).vertices.get(v),
                                kfObjFiles.get(kf_i).vertices.get(v),
                                kfObjFiles.get(kf_i).vertices.get(v), 0.5f, (float)u/n_of_frames);
                        tmp.vertices.add(p);
                    }
                }
                else {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d p = CatmullRom(kfObjFiles.get(kf_i-1).vertices.get(v),
                                kfObjFiles.get(kf_i).vertices.get(v),
                                kfObjFiles.get(kf_i+1).vertices.get(v),
                                kfObjFiles.get(kf_i+2).vertices.get(v), 0.5f, (float)u/n_of_frames);
                        tmp.vertices.add(p);
                    }
                }
                tmp.keyFrame = tmp.keyFrame + u/n_of_frames;
                outObjFiles.add(tmp);
            }
        }
        */
        /*
        int n_frames = 30;
        int n_vertices = kfObjFiles.get(0).vertices.size();
        for (int i = 0; i < kfObjFiles.size(); i++) {
            // We create temporary array of obj files to store interpolated values
            for (int j=0; j <= n_frames; j++) {
                ObjFile tmp_obj = new ObjFile(kfObjFiles.get(i));
                tmp_obj.vertices.clear();
                if (i == 0) {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d tmp_point = CatmullRom(kfObjFiles.get(i).vertices.get(v),
                                                       kfObjFiles.get(i).vertices.get(v),
                                                       kfObjFiles.get(i+1).vertices.get(v),
                                                       kfObjFiles.get(i+2).vertices.get(v), 0.5f, (float) j/n_frames);
                        tmp_obj.vertices.add(tmp_point);
                    }
                } else if (i == kfObjFiles.size()-2) {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d tmp_point = CatmullRom(kfObjFiles.get(i-1).vertices.get(v),
                                                    kfObjFiles.get(i).vertices.get(v),
                                                    kfObjFiles.get(i+1).vertices.get(v),
                                                    kfObjFiles.get(i+1).vertices.get(v), 0.5f, (float) j/n_frames);
                        tmp_obj.vertices.add(tmp_point);
                    }
                } else if (i == kfObjFiles.size()-1) {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d tmp_point = CatmullRom(kfObjFiles.get(i-1).vertices.get(v),
                                                    kfObjFiles.get(i).vertices.get(v),
                                                    kfObjFiles.get(i).vertices.get(v),
                                                    kfObjFiles.get(i).vertices.get(v), 0.5f, (float) j/n_frames);
                        tmp_obj.vertices.add(tmp_point);
                    }
                } else {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d tmp_point = CatmullRom(kfObjFiles.get(i-1).vertices.get(v),
                                                    kfObjFiles.get(i).vertices.get(v),
                                                    kfObjFiles.get(i+1).vertices.get(v),
                                                    kfObjFiles.get(i+2).vertices.get(v), 0.5f, (float) j/n_frames);
                        tmp_obj.vertices.add(tmp_point);
                    }
                }
                outObjFiles.add(tmp_obj);
            }
        }
         */
        int n_kf = kfObjFiles.size();
        int n_vertices = kfObjFiles.get(0).vertices.size();
        for (int i_kf = 0; i_kf < n_kf-1; i_kf++) {
            int n_of_frames = Math.abs((kfObjFiles.get(i_kf+1).keyFrame - kfObjFiles.get(i_kf).keyFrame)/frameRate);
            for (int u=0; u < n_of_frames; u++) {
                ObjFile tmp_obj = new ObjFile(kfObjFiles.get(i_kf));
                tmp_obj.vertices.clear();
                tmp_obj.keyFrame = tmp_obj.keyFrame + (u/n_of_frames * frameRate);

                if (i_kf == 0) {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d tmp_point = CatmullRom(kfObjFiles.get(i_kf).vertices.get(v),
                                kfObjFiles.get(i_kf).vertices.get(v),
                                kfObjFiles.get(i_kf+1).vertices.get(v),
                                kfObjFiles.get(i_kf+2).vertices.get(v), 0.5f, (float) u/n_of_frames);
                        tmp_obj.vertices.add(tmp_point);
                    }
                } else if (i_kf == kfObjFiles.size()-2) {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d tmp_point = CatmullRom(kfObjFiles.get(i_kf-1).vertices.get(v),
                                kfObjFiles.get(i_kf).vertices.get(v),
                                kfObjFiles.get(i_kf+1).vertices.get(v),
                                kfObjFiles.get(i_kf+1).vertices.get(v), 0.5f, (float) u/n_of_frames);
                        tmp_obj.vertices.add(tmp_point);
                    }
                } else {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d tmp_point = CatmullRom(kfObjFiles.get(i_kf-1).vertices.get(v),
                                kfObjFiles.get(i_kf).vertices.get(v),
                                kfObjFiles.get(i_kf+1).vertices.get(v),
                                kfObjFiles.get(i_kf+2).vertices.get(v), 0.5f, (float) u/n_of_frames);
                        tmp_obj.vertices.add(tmp_point);
                    }
                }
                outObjFiles.add(tmp_obj);
            }
        }
    }

    public static void endingFrame() {
        if (Math.abs(endTimeRange - startTimeRange) % frameRate != 0) {
            return;
        }
        for(ObjFile obj : kfObjFiles) {
            if (endTimeRange == obj.keyFrame) {
                outObjFiles.add(obj);
            }
        }
    }

    public static void generateFrames() {
        int n_kf = kfObjFiles.size();
        int n_vertices = kfObjFiles.get(0).vertices.size();
        for (int i_kf = 0; i_kf < n_kf-1; i_kf++) {
            int n_of_frames = Math.abs((kfObjFiles.get(i_kf+1).keyFrame - kfObjFiles.get(i_kf).keyFrame));
            for (int u=0; u < n_of_frames; u++) {
                ObjFile tmp_obj = new ObjFile(kfObjFiles.get(i_kf));
                tmp_obj.vertices.clear();
                tmp_obj.keyFrame = tmp_obj.keyFrame + u;

                if (startTimeRange > tmp_obj.keyFrame || (tmp_obj.keyFrame - startTimeRange)%frameRate != 0) {
                    continue;  // We don't need this frame so we can skip it
                } else if (endTimeRange < tmp_obj.keyFrame) {
                    return;  // We calculated all we need and can return
                }

                if (i_kf == 0) {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d tmp_point = CatmullRom(kfObjFiles.get(i_kf).vertices.get(v),
                                kfObjFiles.get(i_kf).vertices.get(v),
                                kfObjFiles.get(i_kf+1).vertices.get(v),
                                kfObjFiles.get(i_kf+2).vertices.get(v), 0.5f, (float) u/n_of_frames);
                        tmp_obj.vertices.add(tmp_point);
                    }
                } else if (i_kf == kfObjFiles.size()-2) {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d tmp_point = CatmullRom(kfObjFiles.get(i_kf-1).vertices.get(v),
                                kfObjFiles.get(i_kf).vertices.get(v),
                                kfObjFiles.get(i_kf+1).vertices.get(v),
                                kfObjFiles.get(i_kf+1).vertices.get(v), 0.5f, (float) u/n_of_frames);
                        tmp_obj.vertices.add(tmp_point);
                    }
                } else {
                    for (int v = 0; v < n_vertices; v++) {
                        Point3d tmp_point = CatmullRom(kfObjFiles.get(i_kf-1).vertices.get(v),
                                kfObjFiles.get(i_kf).vertices.get(v),
                                kfObjFiles.get(i_kf+1).vertices.get(v),
                                kfObjFiles.get(i_kf+2).vertices.get(v), 0.5f, (float) u/n_of_frames);
                        tmp_obj.vertices.add(tmp_point);
                    }
                }
                outObjFiles.add(tmp_obj);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println("Usage: java animate [framerate] [start timerange] [end timerange] [input filename]");
            System.exit(1);
        }
        frameRate = Integer.parseInt(args[0]);
        startTimeRange = Integer.parseInt(args[1]);
        endTimeRange = Integer.parseInt(args[2]);
        if (endTimeRange <= startTimeRange) {
            System.out.println("Wrong inputs. Start time range should be before end time range.");
            System.exit(1);
        }
        ReadKeyFramesFile(args[3]);

        generateFrames();
        endingFrame();  // We check if we need last frame

        for (int i = 0; i < outObjFiles.size(); i++) {
            ObjFile tmp = outObjFiles.get(i);
            System.out.printf("Generated file: ./output/out_%04d.obj @frame %d\n", i, tmp.keyFrame);
            tmp.writeObj(String.format("./output/out_%04d.obj", i));
        }


    }
}

class ObjFile {
    public ArrayList<Point3d> vertices = new ArrayList<Point3d>();
    public ArrayList<String> strings = new ArrayList<String>();
    public int keyFrame = 0;

    public void ReadObjFile(String file_path) {
        try {
            File myObj = new File(file_path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().strip().split(" ");
                switch (data[0]) {
                    case "v":
                        float x = Float.parseFloat(data[1]);
                        float y = Float.parseFloat(data[2]);
                        float z = Float.parseFloat(data[3]);
                        this.vertices.add(new Point3d(x, y, z));
                        this.strings.add(String.format("%s\n", String.join(" ", data)));
                        break;
                    default:
                        this.strings.add(String.format("%s\n", String.join(" ", data)));
                        break;
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void writeObj(String file_path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file_path));
        writer.write("# This file uses centimeters as units for non-parametric coordinates.\n" + "\n");
        int i = 0;
        for (String line : this.strings) {
            if (line.split(" ")[0].equals("v")) {
                Point3d tmp = this.vertices.get(i);
                writer.write(String.format("v %.6f %.6f %.6f\n", tmp.x, tmp.y, tmp.z));
                i++;
            } else {
                writer.write(line);
            }
        }
        writer.close();
    }

    public ObjFile(String file_path) {
        ReadObjFile(file_path);
    }

    public ObjFile(ObjFile obj) {
        this.vertices = (ArrayList<Point3d>) obj.vertices.clone();
        this.strings = (ArrayList<String>) obj.strings.clone();
        this.keyFrame = obj.keyFrame;
    }
}