package com.awesomeJdk.file;

import com.awesomeJdk.date.FgDateUtils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;

/**
 * @author froggengo@qq.com
 * @date 2021/4/21 8:19.
 */
public class File1Example {

    String str = "有的时候我们不仅仅要对文件做MD5，也需要对字符串String进行MD5操作。下面的方法使用Guava写了一个生成字符串MD5的方法。\n";

    @Test
    public void testFilePath() {
        //项目的工作路径
        System.out.println(System.getProperty("user.dir"));
        //绝对路径，window下是D:\log\log.txt
        File file = new File("\\log\\log.txt");
        System.out.println(file.getAbsolutePath());
        File file2 = new File("log\\log.txt");
        System.out.println(file2.getAbsolutePath());
    }

    @Test
    public void testMkdir() throws IOException {
        File file1 = new File("log/log");
        File file2 = new File("test/log");
        System.out.println(file1.isDirectory());//false
        System.out.println(file1.isFile());//false
        System.out.println(file1.mkdirs());//true
        System.out.println(file2.mkdir());//false
        System.out.println(file1.isDirectory());//true
        System.out.println(file1.isFile());//false
    }

    @Test
    public void testDelete() {
        File parentFile = new File("log/log");
        parentFile.mkdirs();
        File file1 = new File("log/log/1.txt");
        try {
            System.out.println(file1.createNewFile());//true
            //目录下由文件无法删除
            System.out.println(parentFile.delete());//false
            //删除该目录下所有文件
            System.out.println(file1.delete());//true
            //该目录才可以删除
            System.out.println(parentFile.delete());//true
            //退出jvm时删除文件
            file1.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFileProperty() {
        File file1 = new File("log/1.txt");
        try {
            boolean newFile = file1.createNewFile();
            if (newFile || file1.exists()) {
                System.out.println(file1.canExecute());
                System.out.println(file1.canRead());
                System.out.println(file1.canWrite());
                //是否隐藏
                System.out.println(file1.isHidden());
                //最后修改时间
                System.out.println(FgDateUtils.millsToDateTime(file1.lastModified()));
                //文件大小 字节为单位
                System.out.println(file1.length());
            }
        } catch (IOException e) {

        }
    }

    @Test
    public void testListFile() {
        //列出根目录，window下时C:\、d:\、e:\
        File[] files = File.listRoots();
        Arrays.stream(files).forEach(System.out::println);
        File file = new File("d:\\");
        //列出指定文件下的所有文件和文件夹
        Arrays.stream(Objects.requireNonNull(file.listFiles()))
            .forEach(n -> {
                System.out.println(n.getName() + (n.isDirectory() ? "(directory)" : "(file)"));
                if (n.isFile()) {
                    StringBuilder result = createFileMd5(n);
                    System.out.println(result);
                }
            });
        //指定file过滤
        System.out.println(Arrays.stream(Objects.requireNonNull(file.listFiles(File::isFile))).count());
    }

    private StringBuilder createFileMd5(File n) {
        StringBuilder result = new StringBuilder();
        try (FileInputStream fileInputStream = new FileInputStream(n)) {
            final byte[] bytes = fileInputStream.readAllBytes();
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            final byte[] digest = md5.digest();
            for (int i = 0; i < digest.length; i++) {
                result.append(Integer.toString((digest[i] & 0xff) + 0x100, 16)
                                  .substring(1));
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Before
    public void createFile() throws IOException {
        final File file = new File("log/1.txt");
        if (file.createNewFile()) {
            final FileOutputStream fileOutputStream = new FileOutputStream(file);

            for (int i = 0; i < 1000_000; i++) {
                fileOutputStream.write(str.getBytes());
            }
            fileOutputStream.close();
            System.out.println("创建文件" + file.length() / 1000 / 1000d);
        }
    }

    @Test
    public void testBufferedStream() {
        //buffer:148/103、fileinputStream：128/90、randomAccess：109/83,,map:11
        int size = 8192;
        //buffer:174/116,fileinputStream:959/411、randomaccess：724/558
        //int size= 1024;
        final long begin = System.currentTimeMillis();
        try (final BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream("log/1.txt"))) {
            final byte[] bytes = new byte[size];
            while (inputStream.read(bytes) != -1) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("BufferedInputStream time:" + (System.currentTimeMillis() - begin));

        final long begin1 = System.currentTimeMillis();
        try (final FileInputStream inputStream = new FileInputStream("log/1.txt")) {
            final byte[] bytes = new byte[size];
            while (inputStream.read(bytes) != -1) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("time:" + (System.currentTimeMillis() - begin1));

        final long begin2 = System.currentTimeMillis();
        try (RandomAccessFile file = new RandomAccessFile("log/1.txt", "rw");) {
            final byte[] bytes = new byte[size];
            while (file.read(bytes) != -1) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("time:" + (System.currentTimeMillis() - begin2));

        final long begin3 = System.currentTimeMillis();
        final File fileDes = new File("log/1.txt");
        try (RandomAccessFile file = new RandomAccessFile(fileDes, "rw");) {
            final MappedByteBuffer map = file.getChannel().map(MapMode.READ_ONLY, 0, fileDes.length());
            //final CharBuffer decode = Charset.forName("UTF-8").decode(map);
            //System.out.println(decode);
            map.clear();
            //false ,并没有载入内存
            System.out.println(map.isLoaded());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.gc();
        System.out.println("time:" + (System.currentTimeMillis() - begin3));

    }

    @Test
    public void testRandomAccessFile() throws IOException {
        final RandomAccessFile file = new RandomAccessFile("log/1.txt", "rw");
        System.out.println(file.getFilePointer());
        System.out.println(file.length());
    }

    @Test
    public void testRandomAccess() {
        try {
            final RandomAccessFile file = new RandomAccessFile("log/1.txt", "rw");
            file.seek(str.getBytes().length);
            //这里的readline也是一个字节一个字节读，所以效率会非常慢
            final String s = file.readLine();
            ////反编码回文件中本原始的字节流
            final byte[] rawBytes = s.getBytes(StandardCharsets.ISO_8859_1);
            // //String 构造函数默认接受 UTF-8 编码
            System.out.println(new String(rawBytes));
            //此时文件指针指向读取的最新位置
            System.out.println(file.getFilePointer());
            final FileChannel channel = file.getChannel();
            System.out.println(channel.position());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFileChannel() throws IOException {
        final RandomAccessFile file = new RandomAccessFile("log/f1.txt", "rw");
        final FileChannel channel = file.getChannel();
        final ByteBuffer allocate = ByteBuffer.allocate(1024);
        allocate.put("123456789".getBytes());
        allocate.flip();
        channel.write(allocate);
        file.close();
    }

    @Test
    public void testByteBuffer() {
        final ByteBuffer buffer = ByteBuffer.allocate(1024);
        final ByteBuffer direct = ByteBuffer.allocateDirect(1024);
        buffer.put(str.getBytes());
        buffer.flip();
        final byte[] bytes = new byte[1024];
        final ByteBuffer byteBuffer = buffer.get(bytes, 3, buffer.remaining());
        System.out.println(new String(bytes));
    }

    @Test
    public void testBuffer2() throws IOException {
        // world!
        final FileInputStream inputStream = new FileInputStream("log/f1.txt");
        final File file = new File("log/f2.txt");
        file.createNewFile();

        final ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("hello".getBytes());
        //继续将f1.txt填充至ByteBuffer中
        System.out.println(inputStream.getChannel().read(buffer));

        //须调用flip翻转
        buffer.flip();
        final FileOutputStream outputStream = new FileOutputStream(file);
        System.out.println(outputStream.getChannel().write(buffer));

        outputStream.close();
        inputStream.close();
    }

    @Test
    public void testBytebuffer3() throws IOException {
        //获取当前系统文件的编码格式
        String encoding = System.getProperty("file.encoding");
        final Charset charset = Charset.forName(encoding);
        final CharsetDecoder decoder = charset.newDecoder();
        final File file = new File("log/1.txt");
        final FileInputStream fileInputStream = new FileInputStream(file);
        final FileChannel channel = fileInputStream.getChannel();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //将ByteBuffer同过charset转换为中文字符，解决中文乱码
        final CharBuffer charBuffer = CharBuffer.allocate(1024);
        byte[] temp;
        while (channel.read(byteBuffer) != -1) {
            byteBuffer.flip();//切换模式
            decoder.decode(byteBuffer, charBuffer, true);
            charBuffer.flip();
            System.out.print(charBuffer.toString());
            charBuffer.clear();
            //此时由于utf-8存储需要2个字节，所以再边界可能操作同一个字符被分割再两个ByteBuffer上
            //compact()可以将position和limit的字节移动到开头
            //否则会出现再边界处乱码，网上很多解决方法都是手动保存再一个byte[]数组再写入byteBuffer，
            //其实调用这个方法就可以了
            byteBuffer.compact();
        }
        fileInputStream.close();
    }

    @Test
    public void testMappedByteBuffer() throws IOException {
        final File file = new File("log/fb.txt");
        file.createNewFile();
        final RandomAccessFile fileOutputStream = new RandomAccessFile(file, "rw");
        final FileChannel channel = fileOutputStream.getChannel();
        final MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, 1024);
        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();
        //由于内存映射，这里只需要将字符串字节写入MappedByteBuffer
        map.put(byteBuffer);
        //RandomAccessFile关闭不意味着释放MappedByteBuffer占用的空间
        fileOutputStream.close();
    }

    @Test
    public void testRandomAccessFile2() throws FileNotFoundException {
        //当上级路径存在时会自动创建，否则报FileNotFoundException
        final RandomAccessFile fileOutputStream = new RandomAccessFile("log/fa.txt", "rw");
    }

    @Test
    public void testCharSet() throws IOException {
        System.out.println(Charset.defaultCharset());
        final Charset charset = Charset.forName(StandardCharsets.UTF_8.name());
        System.out.println(charset);
        System.out.println(Charset.forName(StandardCharsets.ISO_8859_1.name()));
        final byte[] isoBytes = "我是谁？".getBytes(StandardCharsets.ISO_8859_1);
        System.out.println(isoBytes.length);
        final byte[] utfBytes = "我是谁？".getBytes(StandardCharsets.UTF_8);
        System.out.println(utfBytes.length);
        //???? 无法解析
        System.out.println(new String(isoBytes));
        //会自动创建该文件
        final File file = new File("log/fc.txt");
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write("我是谁？我在哪里".getBytes());
        System.out.println("占用字节：" + "我=".getBytes().length);
        final FileInputStream fileInputStream = new FileInputStream(file);
        final byte[] bytes = new byte[4];
        fileInputStream.read(bytes);
        final CharBuffer charBuffer = CharBuffer.allocate(4);
        charset.newDecoder().decode(ByteBuffer.wrap(bytes), charBuffer, true);
        //万恶的flip方法
        charBuffer.flip();
        //String 默认使用utf-8解析字节数组，所以这里会多出一个字节导致乱码
        System.out.println(new String(bytes));
        System.out.println(charBuffer.toString());
        fileOutputStream.close();
    }

    @Test
    public void testFileSystem() throws IOException {
        final FileSystem fileSystem = FileSystems.getDefault();
        for (FileStore fileStore : fileSystem.getFileStores()) {
            System.out.println("名称：" + fileStore.name());
            System.out.println("    type：" + fileStore.type());
            System.out.println("    TotalSpace：" + fileStore.getTotalSpace() / 1024d / 1024d / 1024d);
            System.out.println("    UsableSpace：" + fileStore.getUsableSpace() / 1024d / 1024d / 1024d);
            System.out.println("    UnallocatedSpace：" + fileStore.getUnallocatedSpace() / 1024d / 1024d / 1024d);
        }
    }

    @Test
    public void testFileSystem2() throws IOException {
        final File file = new File("log/fs.txt");
        file.getCanonicalPath();
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        final FileSystem fileSystem = FileSystems.getDefault();
        final WatchService watchService = fileSystem.newWatchService();
        WatchKey poll;
        while ((poll = watchService.poll()) != null) {
            System.out.println("触发事件");
            poll.pollEvents().forEach(n -> System.out.println(n.kind().name()));
        }
        fileOutputStream.write("hello world!".getBytes());
        fileOutputStream.close();
    }

    @Test
    public void testPath() throws IOException {
        final Path path = Path.of("log", "f2.txt");
        System.out.println(Files.exists(path));
        System.out.println(path.normalize());
        System.out.println(path.toAbsolutePath());
        final URI uri = path.toUri();
        final URL url = uri.toURL();
        //file:///D:/%5B4%5Dproject/springcloud/awesome-jdk-practice/log/f2.txt
        System.out.println(uri);
        //特殊符号或中文转码，如[会在uri中表示为%5B，所以需要URLDecoder解析
        System.out.println(URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8));
        final InputStream inputStream = url.openStream();
        final byte[] bytes = new byte[1024];
        inputStream.read(bytes);
        System.out.println(new String(bytes));
        inputStream.close();
        //url获取http资源
        final URL baidu = new URL("https://www.baidu.com/");
        System.out.println(baidu);
        final URLConnection connection = baidu.openConnection();
        final InputStream baiduStream = connection.getInputStream();
        final byte[] baiduBytes = baiduStream.readAllBytes();
        System.out.println(new String(baiduBytes));
        baiduStream.close();
    }

    @Test
    public void testPathOp() throws IOException {
        //创建
        final Path basePath = Path.of("aaaa", "bbb", "ccc");
        //创建不存在的多级目录，aaaa\bbb\ccc
        Files.createDirectories(basePath);
        final Path other = Path.of("test/log");
        //other为绝对路径直接返回，否则返回两个连接的路径：path\other
        //aaaa\bbb\ccc\test\log
        System.out.println(basePath.resolve(other));
        //在basePath的上目录下连接other,如path\..\other
        //aaaa\bbb\test\log
        System.out.println(basePath.resolveSibling(other));
        //返回在 basePath 下访问other的相对路径,一定要注意是在basePath下访问other的相对路径
        //..\..\..\test\log
        System.out.println(basePath.relativize(other));
        //这里很奇怪,输出结果还是..\..\..\test\log
        //此时的路径是相对于当前根目录,和上面相对与basePath不一样
        System.out.println(basePath.relativize(other).normalize());
        //这里保留了相对路径:D:\[4]project\springcloud\awesome-jdk-practice\..\..\..\test\log
        System.out.println(basePath.relativize(other).toAbsolutePath());
        //去掉了冗余的相对路径的符号D:\test\log
        System.out.println(basePath.relativize(other).toAbsolutePath().normalize());
        //比较
        final Path otherAbs = other.toAbsolutePath();
        //如果相等返回0,否则返回词典排序的差值
        System.out.println(other.compareTo(otherAbs));
        //这里是false
        System.out.println(other.equals(otherAbs));
    }

    @Test
    public void testPathMatcher() throws IOException {
        final Path parent = Path.of(".");
        final DirectoryStream<Path> paths = Files.newDirectoryStream(parent,"*.xml");
        paths.forEach(System.out::println);
        Files.newDirectoryStream(parent,path->path.toFile().isFile()).forEach(System.out::println);
        //底层也是使用newDirectoryStream
        Files.list(parent);
    }

    @Test
    public void testFileVisitor() throws IOException {
        //遍历目录
        final Path dpath = Path.of(".");
        //深度优先搜索返回指定Path下的所有文件和文件夹
        final Stream<Path> walk = Files.walk(dpath.toAbsolutePath());
        walk.forEach(System.out::println);
        System.out.println("=============");
        final Path source = Path.of("log");
        final Path target = Path.of("aaaa");
        //遍历目录树，并使用FileVisitor完成文件目录以及文件的拷贝
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                final Path targetdir = target.resolve(source.relativize(dir));
                Files.copy(dir, targetdir, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    @Test
    public void testFileVisitor2 () throws IOException {
        final Path path = Path.of("D:\\[4]project\\springcloud\\awesome-jdk-practice\\src\\main\\java");
        Files.walkFileTree(path,new SimpleFileVisitor<>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file.getFileName()+"="+createFileMd5(file.toFile()));
                return FileVisitResult.CONTINUE;
            }
        });
    }
    @Test
    public void testFiles1 () throws IOException {
        //默认是UTF_8，这里也可以指定任意编码格式
        System.out.println(Files.readString(Path.of("log/fc.txt"), StandardCharsets.UTF_8));
        //默认写配置 Set.of(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE);
        System.out.println(Files.write(Path.of("log/ffs.txt"), "我知道你是谁！".getBytes(), StandardOpenOption.APPEND));
    }
    @Test
    public void testFiles2() throws IOException {
        final Path path = Path.of("pom.xml");
        final BufferedReader reader = Files.newBufferedReader(path);
        System.out.println(reader.readLine());
        reader.close();
        final Path file = Path.of("log/fw.txt");
        final BufferedWriter writer = Files.newBufferedWriter(file);
        writer.write(str);
        writer.close();
        System.out.println(Files.size(file));
        System.out.println(file.toFile().length());
    }
    @Test
    public void testZip() {
        //new ZipFile()
    }

    @Test
    public void test() {
        Manifest manifest = new Manifest();
        manifest.getEntries().forEach((k, v) -> System.out.println(k + "=" + v));
    }
}
