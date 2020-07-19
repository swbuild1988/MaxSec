package com.bandweaver.maxsec.util;

import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

@Slf4j
public class FileUtils {

    public static String saveTemp(Pointer pointer, int length) {
        if (length > 0) {
            FileOutputStream fout;
            try {
                String fileName = System.getProperty("java.io.tmpdir") + "\\" + UUID.randomUUID() + ".jpg";
                fout = new FileOutputStream(fileName);
                //将字节写入文件
                long offset = 0;
                ByteBuffer buffers = pointer.getByteBuffer(offset, length);
                byte[] bytes = new byte[length];
                buffers.rewind();
                buffers.get(bytes);
                fout.write(bytes);
                fout.close();
                return fileName;
            } catch (Exception ex) {
                log.error("save file error", ex);
                return null;
            }
        }
        return null;
    }

    public static byte[] getFileByte(String fileName) {
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            int i = inputStream.available();
            // byte数组用于存放图片字节数据
            byte[] buff = new byte[i];
            inputStream.read(buff);
            // 关闭输入流
            inputStream.close();
            return buff;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
