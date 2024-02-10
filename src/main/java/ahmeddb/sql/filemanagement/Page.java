package ahmeddb.sql.filemanagement;

import ahmeddb.sql.configuration.DataSourceConfigProvider;
import ahmeddb.sql.logmanagement.LogRecord;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.charset.Charset;

/**
 * A Page object holds the contents of a disk block (creating a byte buffer for it).
 * The various get and set methods enable clients to store or access values at specified locations of the page. Also, they are
 * wrappers for the original methods of {@link ByteBuffer} class. <br/>
 * A client can store a value at any offset of the page but is responsible for knowing what values have been stored where.
 * An attempt to get a value from the wrong offset will have unpredictable results.<br/>
 * The page is responsible for dealing with memory, (i.e: acts as if it's a memory page).
 * so to read some data from file, we need to load it in a page object first.
 * Then, any modification of that data at the page object can be written to the file.
 * The read and write operations are used by {@link FileManager}.<br/>
 *
 * <h2> Direct <i>vs.</i> non-direct buffers </h2>
 * <p><strong>Direct ByteBuffer</strong>: When you allocate a direct ByteBuffer, memory is allocated outside of the JVM's heap space.
 * This memory is typically allocated by the operating system and is managed by the JVM through native code.
 * Direct ByteBuffer objects are often preferred for I/O operations as they can be more efficient for tasks like reading
 * from or writing to files, sockets, and other I/O channels.</p>
 *
 * <p><strong>Indirect ByteBuffer:</strong> In contrast, when you allocate an indirect ByteBuffer, memory is allocated within
 * the JVM's heap space like any other object. These are the traditional ByteBuffer objects allocated
 * via ByteBuffer.allocate(). Indirect buffers have the benefit of being easier to work with and can be more
 * flexible in terms of resizing and garbage collection, but they may incur additional overhead when performing
 * I/O operations due to the need for copying data between the buffer and native I/O buffers.</p>
 * <br/>
 * Direct ByteBuffer objects are often used in scenarios where performance is critical, especially for large data transfers, as they can offer better performance by avoiding unnecessary data copying between JVM and native memory. However, they come with some considerations, such as potentially higher memory overhead and limitations on direct memory allocation imposed by the operating system.
 *
 * @see <a href="https://www.baeldung.com/java-bytebuffer">Baeldung: Java-bytebuffer</a>
 * and <a href="https://stackoverflow.com/questions/5670862/bytebuffer-allocate-vs-bytebuffer-allocatedirect">Stackoverflow: bytebuffer-allocate-vs-bytebuffer-allocatedirect</a>
 */
public class Page {
    /**
     * The conversion between a string and its byte representation is determined by a character encoding.
     * Several standard encodings exist, such as ASCII and Unicode16.
     * The Java Charset class contains objects that implement many of these encodings.
     * The constructor for String and its getBytes method take a Charset argument.
     * Page uses the ASCII encoding by default, but you can change the CHARSET constant to get an encoding of your preference.
     * A charset chooses how many bytes each character encodes to.
     * ASCII uses one byte per character, whereas Unicode-16 uses between 2 bytes and 4 bytes per character.
     */
    private static final Charset CHARSET = DataSourceConfigProvider.getDataSourceConfig().getDbCharset();

    /**
     * <p>
     * Each page is implemented using a ByteBuffer object that considered as (or hold) a page contents.
     * A ByteBuffer object wraps a byte array with methods to read and write values at arbitrary locations of the array.
     * These values can be primitive values (such as integers) as well as smaller byte arrays.
     * For example, Page’s setInt method saves an integer in the page by calling the ByteBuffer’s putInt method.
     * Page’s setString method saves a blob as two values: first the number of bytes in the specified blob and then
     * the bytes themselves. It calls ByteBuffer’s putInt method to write the integer and the method put to write the bytes.
     * </p>
     * <p>
     * The byte array that underlies a ByteBuffer object can come either from a Java array or
     * from the operating system’s I/O buffers. The Page class has two constructors,
     * each corresponding to a different kind of underlying byte array.
     * Since I/O buffers are a valuable resource, the use of the first constructor is
     * carefully controlled by the buffer manager. Other components of the database engine
     * (such as the log manager) use the other constructor.
     * </p>
     */
    private final ByteBuffer byteBuffer;

    /**
     * The constructor creates a page that gets its memory from an operating system I/O buffer.
     * this constructor is used by the buffer manager to deal with data of db files, such as reading from it,
     * or writing to it.<br/>
     * The memory allocated size of the byteBuffer will be got from the DB configuration.
     */
    public Page (){
        //blockSize is the size of file block to allocate a direct buffer in memory space for its size.
        this.byteBuffer = ByteBuffer.allocateDirect(DataSourceConfigProvider.getDataSourceConfig().getBlockSize());
    }

    /**
     * The constructor creates a page that gets its memory from a Java array;
     * this constructor is used primarily by the log manager, as we don't rely on a specific blockSize such as first
     * constructor, because we store data in log files only that have another way of dealing it,
     * unlike the first constructor who cares about the blockSize,
     * because we use that first constructor for writing or reading our real data from db files.
     * this constructor cares only about dealing with log files.
     * As far as the log manager is concerned, a log record is an arbitrarily sized byte array;
     * it saves the array in the log file but has no idea what its contents denotes.
     * Each block in the log file may contain more than 1 log record as the Log records can have varying sizes.
     * Each log page will have its size for a specific record bytes only, NO record will have equal amount of bytes to
     * another record, it depends on the record data.
     */
    public Page(byte[] bytes){
        this.byteBuffer = ByteBuffer.wrap(bytes);
    }


    /**
     * Absolute <i>get</i> method for reading an int value.
     * <p> Reads four bytes at the given index, composing them into a
     * int value according to the current byte order.</p>
     *
     * @apiNote The byteBuffer position won't be changed, so take care of setting the appropriate index, otherwise unpredictable data will be obtained.
     *
     * @param  index The index from which the bytes will be read
     * @return  The int value at the given index
     * @throws IndexOutOfBoundsException If {@code index} is negative or not smaller than the buffer's limit, minus three
     * @throws ReadOnlyBufferException If this buffer is read-only
     */
    public int getInt(int index){
       return byteBuffer.getInt(index);
    }

    /**
     * Absolute <i>put</i> method for writing an int
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes four bytes containing the given int value, in the
     * current byte order, into this buffer at the given index.  </p>
     *
     * @apiNote The byteBuffer position won't be changed, so take care of setting the appropriate index, otherwise unpredictable data will be obtained.
     *
     * @param  index The index at which the bytes will be written
     * @param  value The int value to be written
     **
     * @throws  IndexOutOfBoundsException If {@code index} is negative or not smaller than the buffer's limit, minus three
     * @throws  ReadOnlyBufferException If this buffer is read-only
     */
    public void setInt(int index, int value){
        byteBuffer.putInt(index, value);
    }

    /**
     * The ByteBuffer class does not have methods to read and write strings, so
     * Page chooses to write string values as blobs.
     * The Java String class has a method getBytes, which converts a string into a byte array.
     * it also has a constructor that converts the byte array back to a string. Thus, Page’s setString method calls
     * getBytes to convert the string to bytes and then writes those bytes as a blob.
     * For example, tne string stored in buffer like this: [Int, byte1,byte2, byte3], the string is composed of these bytes [byte1, byte2, byte3] and the Int in this case is 3
     *
     * @apiNote The byteBuffer position will be changed, so take care of setting the appropriate index, otherwise unpredictable data will be obtained.
     *
     * @param index The index at which the bytes will be written, but in this case,
     *              we put in it an integer number that represent the string bytes length (string bytes length),
     *              so we can convert back these bytes to real string value.
     * @param value the string that will be stored in byteBuffer object.
     */
    public void setString(int index, String value){
        byte[] stringBytes = value.getBytes(CHARSET);
        byteBuffer.position(index);
        byteBuffer.putInt(stringBytes.length);
        byteBuffer.put(stringBytes, 0, stringBytes.length);
    }

    /**
     * Reads a blob from the byte buffer and then converts the bytes to a string
     *
     * @apiNote The byteBuffer position will be changed, so take care of setting the appropriate index, otherwise unpredictable data will be obtained.
     *
     * @param index The index at which the bytes will be read, but in this case,
     *              we get from it an integer number that represent the string bytes length (string bytes length),
     *              so we can convert back these bytes to real string value by calculating the bytes length to convert them back.
     * @return String (real text)
     */
    public String getString(int index){
        byteBuffer.position(index);
        int strLength = byteBuffer.getInt();
        byte[] stringBytes = new byte[strLength];
        byteBuffer.get(stringBytes, 0, stringBytes.length);

        return new String(stringBytes,CHARSET);
    }

    public void setBytes(int index , byte[] bytes){
        byteBuffer.put(index,bytes);
    }


    /**
     * Get page's content, the content is represented as a sequence of bytes stored in byteBuffer object that
     * acts as array of bytes.
     * @return page's byteBuffer object.
     */
    public ByteBuffer contents(){
        byteBuffer.position(0);
        return byteBuffer;
    }

    public int getStringBytesLength(String value){
        return value.getBytes(CHARSET).length;
    }

    /**
     * Clearing page's contents
     */
    public ByteBuffer clear(){
        return byteBuffer.clear();
    }

}