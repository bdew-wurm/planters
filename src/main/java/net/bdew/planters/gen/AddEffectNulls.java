package net.bdew.planters.gen;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class AddEffectNulls {
    private static String readString(LittleEndianDataInputStream in) throws IOException {
        int len = in.readInt();
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++)
            b[i] = in.readByte();
        return new String(b, StandardCharsets.UTF_8);
    }

    private static void writeString(LittleEndianDataOutputStream out, String s) throws IOException {
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        out.writeInt(b.length);
        for (byte value : b)
            out.writeByte(value);
    }

    private static float[] readMatrix(LittleEndianDataInputStream in) throws IOException {
        float[] mat = new float[16];
        for (int i = 0; i < 16; i++) {
            mat[i] = in.readFloat();
        }
        return mat;
    }

    private static void writeMatrix(LittleEndianDataOutputStream out, float[] mat) throws IOException {
        for (int i = 0; i < 16; i++) {
            out.writeFloat(mat[i]);
        }
    }

    private static String printMatrix(float[] mat) {
        String[] s = new String[mat.length];
        for (int i = 0; i < 16; i++) {
            s[i] = Float.toString(mat[i]);
        }
        return String.join(" ", s);
    }

    static class Joint {
        public final String name, parent;
        public final float[] mat1, mat2;
        public final boolean isCOB;

        public Joint(String name, String parent, float[] mat1, float[] mat2, boolean isCOB) {
            this.name = name;
            this.parent = parent;
            this.mat1 = mat1;
            this.mat2 = mat2;
            this.isCOB = isCOB;
        }

        public static Joint create(String name, float x, float y, float z) {
            float[] mat1 = new float[16];
            float[] mat2 = new float[16];
            Arrays.fill(mat1, 0f);
            Arrays.fill(mat2, 0f);
            mat1[0] = mat2[0] = mat1[5] = mat2[5] = mat1[10] = mat2[10] = mat1[15] = mat2[15] = 1;
            mat1[3] = x;
            mat1[7] = y;
            mat1[11] = z;
            return new Joint(name, "", mat1, mat2, false);
        }
    }

    private static void process(File inFile, Joint... toAdd) throws IOException {
        int totalFaces = 0;
        int totalVerts = 0;
        int totalMats = 0;
        File outFile = File.createTempFile("tmp", "");
        try (LittleEndianDataInputStream in = new LittleEndianDataInputStream(new BufferedInputStream(new FileInputStream(inFile)))) {
            try (LittleEndianDataOutputStream out = new LittleEndianDataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)))) {

                int meshes = in.readInt();
                out.writeInt(meshes);

                for (int m = 0; m < meshes; m++) {
                    boolean hasTangents = in.read() != 0;
                    boolean hasBinormal = in.read() != 0;
                    boolean hasVertexColor = in.read() != 0;

                    out.write(hasTangents ? 1 : 0);
                    out.write(hasBinormal ? 1 : 0);
                    out.write(hasVertexColor ? 1 : 0);

                    String name = readString(in);
                    writeString(out, name);

                    int verts = in.readInt();
                    out.writeInt(verts);

                    for (int v = 0; v < verts; v++) {
                        out.writeFloat(in.readFloat()); // x
                        out.writeFloat(in.readFloat()); // y
                        out.writeFloat(in.readFloat()); // z
                        out.writeFloat(in.readFloat()); // nx
                        out.writeFloat(in.readFloat()); // ny
                        out.writeFloat(in.readFloat()); // nz
                        out.writeFloat(in.readFloat()); // u
                        out.writeFloat(in.readFloat()); // v
                        if (hasVertexColor) {
                            out.writeFloat(in.readFloat()); // r
                            out.writeFloat(in.readFloat()); // g
                            out.writeFloat(in.readFloat()); // b
                        }
                        if (hasTangents) {
                            out.writeFloat(in.readFloat()); // x
                            out.writeFloat(in.readFloat()); // y
                            out.writeFloat(in.readFloat()); // z
                        }
                        if (hasBinormal) {
                            out.writeFloat(in.readFloat()); // x
                            out.writeFloat(in.readFloat()); // y
                            out.writeFloat(in.readFloat()); // z
                        }
                    }

                    int faces = in.readInt();
                    out.writeInt(faces);

                    for (int v = 0; v < faces; v++)
                        out.writeShort(in.readShort());

                    int mats = in.readInt();
                    out.writeInt(mats);

                    totalFaces += faces;
                    totalVerts += verts;
                    totalMats += mats;

                    for (int mi = 0; mi < mats; mi++) {
                        String tex = readString(in);
                        String matName = readString(in);

                        writeString(out, tex);
                        writeString(out, matName);

                        out.writeByte(in.readByte()); // enabled

                        out.writeByte(in.readByte()); // property exists
                        out.writeFloat(in.readFloat()); // emissive R
                        out.writeFloat(in.readFloat()); // emissive G
                        out.writeFloat(in.readFloat()); // emissive B
                        out.writeFloat(in.readFloat()); // emissive A

                        out.writeByte(in.readByte()); // property exists
                        out.writeFloat(in.readFloat()); // shiny

                        out.writeByte(in.readByte()); // property exists
                        out.writeFloat(in.readFloat()); // specular R
                        out.writeFloat(in.readFloat()); // specular G
                        out.writeFloat(in.readFloat()); // specular B
                        out.writeFloat(in.readFloat()); // specular A

                        out.writeByte(in.readByte()); // property exists
                        out.writeFloat(in.readFloat()); // transparency R
                        out.writeFloat(in.readFloat()); // transparency G
                        out.writeFloat(in.readFloat()); // transparency B
                        out.writeFloat(in.readFloat()); // transparency A
                    }
                }

                int jointsNum = in.readInt();

                System.out.println(String.format("%s: %d meshes / %d vertices / %d faces / %d materials / %d joints", inFile.getName(), meshes, totalVerts, totalFaces / 3, totalMats, jointsNum));

                List<Joint> joints = new ArrayList<>();

                for (int i = 0; i < jointsNum; i++) {
                    String parent = readString(in);
                    String name = readString(in);
                    boolean isChildOfBlend = in.read() != 0;
                    float[] mat1 = readMatrix(in);
                    float[] mat2 = readMatrix(in);
                    joints.add(new Joint(name, parent, mat1, mat2, isChildOfBlend));
                }

                for (Joint j : toAdd) {
                    boolean found = false;
                    for (int i = 0; i < joints.size(); i++) {
                        if (j.name.equals(joints.get(i).name)) {
                            System.out.println(String.format("Replacing joint %s in model", j.name));
                            joints.set(i, j);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println(String.format("Adding joint %s in model", j.name));
                        joints.add(j);
                    }
                }

                out.writeInt(joints.size());

                for (Joint j : joints) {
                    writeString(out, j.parent);
                    writeString(out, j.name);
                    out.write(j.isCOB ? 1 : 0);
                    writeMatrix(out, j.mat1);
                    writeMatrix(out, j.mat2);
//                    System.out.println(String.format("%s -> %s", j.parent, j.name));
//                    System.out.println(" - " + printMatrix(j.mat1));
//                    System.out.println(" - " + printMatrix(j.mat2));
                }

                for (int i = 0; i < meshes; i++) {
                    if (in.read() != 0) System.err.println("Warning mesh uses skinning, currently unsupported!");
                    out.write(0);
                }
            }
        }
        if (!inFile.delete()) throw new RuntimeException("Unable to delete input file");
        if (!outFile.renameTo(inFile)) throw new RuntimeException("Unable to rename output file");
    }


    public static void main(String[] args) {

        try {
            File dir = new File("pack");
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (!file.getName().endsWith(".wom")) continue;
                if (file.getName().contains("shroom-magic") && !file.getName().contains("wilted")) {
                    process(file, Joint.create("sparkle", 0, 0.5f, 0));
                } else if (file.getName().contains("basket-magic") || file.getName().contains("shroom-item")) {
                    process(file, Joint.create("sparkle", 0, 0.1f, 0));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

