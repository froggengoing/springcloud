package com.awesomeJdk.practise.bthread;

import java.io.*;

public class Thread5_pipestream {
    public static void main(String[] args) throws IOException {
        try(PipedReader reader = new PipedReader();
            PipedWriter writer = new PipedWriter()) {
            writer.connect(reader);
            new Thread(() -> {
                int r;
                try {
                    while ((r = reader.read()) != -1) {
                        System.out.print(((char) r));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();
            int c;
            while ((c = System.in.read()) != -1) {
                writer.write(c);
            }

        }

    }
}
