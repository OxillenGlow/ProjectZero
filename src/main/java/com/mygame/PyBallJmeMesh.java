package com.mygame;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for building custom "PyBall" meshes.
 * Generates pyramid-based geometry with explicit normal data to support diffuse lighting.
 */
public class PyBallJmeMesh {
    private static final Mesh[] meshArray = new Mesh[64];

    /**
     * Initializes the mesh cache for all 64 possible face combinations.
     */
    public static void init() {
        for (int i = 0; i < 64; i++) {
            boolean[] faces = new boolean[6];
            for (int j = 0; j < 6; j++) {
                faces[j] = (i & (1 << j)) != 0;
            }
            meshArray[i] = buildMesh(faces);
        }
    }

    /**
     * Returns a cached mesh based on the specified active faces.
     */
    public static Mesh getMesh(boolean px, boolean py, boolean pz, boolean nx, boolean ny, boolean nz) {
        int index = 0;
        int total = (px ? 1 : 0) + (py ? 1 : 0) + (pz ? 1 : 0) + (nx ? 1 : 0) + (ny ? 1 : 0) + (nz ? 1 : 0);
        if (total < 4) {
            if (px) index |= 1;
            if (py) index |= (1 << 1);
            if (pz) index |= (1 << 2);
            if (nx) index |= (1 << 3);
            if (ny) index |= (1 << 4);
            if (nz) index |= (1 << 5);
        } else {
            index = 63;
        }
        return meshArray[index];
    }

    private static Mesh buildMesh(boolean[] faces) {
        List<Vector3f> vertList = new ArrayList<>();
        List<Vector3f> normList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();
        // Add pyramids for each active face
        if (faces[3]) addPyramid(vertList, normList, indexList, new Vector3f(0.5f,0.5f,0.5f), new Vector3f(0.5f,0.5f,-0.5f), new Vector3f(0.5f,-0.5f,-0.5f), new Vector3f(0.5f,-0.5f,0.5f));
        if (faces[0]) addPyramid(vertList, normList, indexList, new Vector3f(-0.5f,0.5f,-0.5f), new Vector3f(-0.5f,0.5f,0.5f), new Vector3f(-0.5f,-0.5f,0.5f), new Vector3f(-0.5f,-0.5f,-0.5f));
        if (faces[4]) addPyramid(vertList, normList, indexList, new Vector3f(-0.5f,0.5f,0.5f), new Vector3f(0.5f,0.5f,0.5f), new Vector3f(0.5f,0.5f,-0.5f), new Vector3f(-0.5f,0.5f,-0.5f));
        if (faces[1]) addPyramid(vertList, normList, indexList, new Vector3f(-0.5f,-0.5f,-0.5f), new Vector3f(0.5f,-0.5f,-0.5f), new Vector3f(0.5f,-0.5f,0.5f), new Vector3f(-0.5f,-0.5f,0.5f));
        if (faces[5]) addPyramid(vertList, normList, indexList, new Vector3f(-0.5f,0.5f,0.5f), new Vector3f(-0.5f,-0.5f,0.5f), new Vector3f(0.5f,-0.5f,0.5f), new Vector3f(0.5f,0.5f,0.5f));
        if (faces[2]) addPyramid(vertList, normList, indexList, new Vector3f(0.5f,0.5f,-0.5f), new Vector3f(0.5f,-0.5f,-0.5f), new Vector3f(-0.5f,-0.5f,-0.5f), new Vector3f(-0.5f,0.5f,-0.5f));


        
        Mesh mesh = new Mesh();
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertList.toArray(new Vector3f[0])));
        mesh.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(normList.toArray(new Vector3f[0])));
        
        int[] indices = new int[indexList.size()];
        for(int i=0; i<indices.length; i++) indices[i] = indexList.get(i);
        mesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        
        mesh.updateBound();
        return mesh;
    }

    /**
     * Adds a pyramid with flat shading by duplicating vertices for each face 
     * to ensure each triangle has a consistent normal.
     */
    private static void addPyramid(List<Vector3f> verts, List<Vector3f> norms, List<Integer> indices, Vector3f b1, Vector3f b2, Vector3f b3, Vector3f b4) {
        Vector3f tip = new Vector3f(0, 0, 0);
        
        // 4 Side faces
        addFace(verts, norms, indices, tip, b1, b2);
        addFace(verts, norms, indices, tip, b2, b3);
        addFace(verts, norms, indices, tip, b3, b4);
        addFace(verts, norms, indices, tip, b4, b1);
        
        // 2 Base faces (quad split into triangles)
        addFace(verts, norms, indices, b1, b4, b3);
        addFace(verts, norms, indices, b1, b3, b2);
    }

    /**
     * Adds a single triangle and calculates its face normal.
     */
    private static void addFace(List<Vector3f> verts, List<Vector3f> norms, List<Integer> indices, Vector3f v1, Vector3f v2, Vector3f v3) {
        int offset = verts.size();
        
        // Calculate face normal using cross product
        Vector3f normal = v2.subtract(v1).crossLocal(v3.subtract(v1)).normalizeLocal();

        verts.add(v1); verts.add(v2); verts.add(v3);
        norms.add(normal); norms.add(normal); norms.add(normal);
        
        indices.add(offset);
        indices.add(offset + 1);
        indices.add(offset + 2);
    }
}
