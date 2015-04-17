package com.android.fyf.sdk.common.toolbox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.util.Base64;

/**
 * Serialize Utils
 * 
 * @author boyang116245@sohu-inc.com
 * @since 2013-11-18
 */
public class SerializeUtils {

    /**
     * deserialization from file
     * 
     * @param filePath
     * @return
     * @throws RuntimeException if an error occurs
     */
    public static Object deserialization(String filePath) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(filePath));
            Object o = in.readObject();
            in.close();
            return o;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("ClassNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * serialize to file
     * 
     * @param filePath
     * @param obj
     * @return
     * @throws RuntimeException if an error occurs
     */
    public static void serialization(String filePath, Object obj) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filePath));
            out.writeObject(obj);
            out.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * 序列化一个对象
     * 
     * @param object
     * @return
     */
    public static String getSerializableString(Serializable object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        String productBase64 = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            productBase64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        } catch (IOException e) {
            LogUtils.e("SerializeUtils", e.toString());
        } catch (OutOfMemoryError e) {
            LogUtils.e("SerializeUtils", e.toString());
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    LogUtils.e("SerializeUtils", e.toString());
                }
            }
        }
        if (productBase64 == null) {
            productBase64 = "";
        }
        return productBase64;
    }

    /**
     * 反序列化一个对象
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static Serializable getSerializableObject(String data) {
        byte[] objBytes = Base64.decode(data.getBytes(), Base64.DEFAULT);
        if (objBytes == null || objBytes.length == 0) {
            return null;
        }
        ByteArrayInputStream bi = null;
        ObjectInputStream oi = null;
        Object object = null;
        try {
            bi = new ByteArrayInputStream(objBytes);
            oi = new ObjectInputStream(bi);
            object = oi.readObject();
        } catch (Exception e) {
            return null;
        } finally {
            if (oi != null) {
                try {
                    oi.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (bi != null) {
                try {
                    bi.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return (Serializable) object;
    }
}
