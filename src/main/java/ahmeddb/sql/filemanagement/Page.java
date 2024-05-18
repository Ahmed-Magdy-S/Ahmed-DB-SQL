package ahmeddb.sql.filemanagement;

import ahmeddb.sql.configuration.DataSourceConfigProvider;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
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
    public Page() {
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
    public Page(byte[] bytes) {
        this.byteBuffer = ByteBuffer.wrap(bytes);
    }


    /**
     * Absolute <i>get</i> method for reading an int value.
     * <p> Reads four bytes at the given index, composing them into a
     * int value according to the current byte order.</p>
     *
     * @param index The index from which the bytes will be read
     * @return The int value at the given index
     * @throws IndexOutOfBoundsException If {@code index} is negative or not smaller than the buffer's limit, minus three
     * @throws ReadOnlyBufferException   If this buffer is read-only
     * @apiNote The byteBuffer position won't be changed, so take care of setting the appropriate index, otherwise unpredictable data will be obtained.
     */
    public int getInt(int index) {
        return byteBuffer.getInt(index);
    }

    /**
     * Relative <i>get</i> method for reading an int value.
     *
     * <p> Reads the next four bytes at this buffer's current position,
     * composing them into an int value according to the current byte order,
     * and then increments the position by four.  </p>
     *
     * @return  The int value at the buffer's current position
     *
     * @throws BufferUnderflowException
     *          If there are fewer than four bytes
     *          remaining in this buffer
     */
    public int getInt(){
        return byteBuffer.getInt();
    }

    /**
     * Absolute <i>put</i> method for writing an int
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes four bytes containing the given int value, in the
     * current byte order, into this buffer at the given index.  </p>
     *
     * @param index The index at which the bytes will be written
     * @param value The int value to be written
     *
     * @throws IndexOutOfBoundsException If {@code index} is negative or not smaller than the buffer's limit, minus three
     * @throws ReadOnlyBufferException   If this buffer is read-only
     * @apiNote The byteBuffer position won't be changed, so take care of setting the appropriate index, otherwise unpredictable data will be obtained.
     */
    public void setInt(int index, int value) {
        byteBuffer.putInt(index, value);
    }

    /**
     * Absolute <i>put</i> method for writing a double
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes eight bytes containing the given double value, in the
     * current byte order, into this page at the given index.  </p>
     *
     * @param  index
     *         The index at which the bytes will be written
     *
     * @param  value
     *         The double value to be written
     *
     * @return  This page
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative
     *          or not smaller than the page's limit,
     *          minus seven
     *
     * @throws  ReadOnlyBufferException
     *          If this page is read-only
     */
    public Page setDouble(int index, double value){
        byteBuffer.putDouble(index,value);
        return this;
    }

    /**
     * Relative <i>put</i> method for writing a double
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes eight bytes containing the given double value, in the
     * current byte order, into this page at the current position, and then
     * increments the position by eight.  </p>
     *
     * @param  value
     *         The double value to be written
     *
     * @return  This page
     *
     * @throws BufferOverflowException
     *          If there are fewer than eight bytes
     *          remaining in this page
     *
     * @throws  ReadOnlyBufferException
     *          If this page is read-only
     */
    public Page setDouble(double value){
        byteBuffer.putDouble(value);
        return this;
    }

    /**
     * Relative <i>get</i> method for reading a double value.
     *
     * <p> Reads the next eight bytes at this page's current position,
     * composing them into a double value according to the current byte order,
     * and then increments the position by eight.  </p>
     *
     * @return  The double value at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than eight bytes
     *          remaining in this page
     */
    public double getDouble(){
       return byteBuffer.getDouble();
    }

    /**
     * Absolute <i>get</i> method for reading a double value.
     *
     * <p> Reads eight bytes at the given index, composing them into a
     * double value according to the current byte order.  </p>
     *
     * @param  index
     *         The index from which the bytes will be read
     *
     * @return  The double value at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative
     *          or not smaller than the page's limit,
     *          minus seven
     */
    public double getDouble(int index){
        return byteBuffer.getDouble(index);
    }

    /**
     * Relative <i>put</i> method for writing a float
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes four bytes containing the given float value, in the
     * current byte order, into this buffer at the current position, and then
     * increments the position by four.  </p>
     *
     * @param  value
     *         The float value to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there are fewer than four bytes
     *          remaining in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public Page setFloat(float value){
        byteBuffer.putFloat(value);
        return this;
    }

    /**
     * Absolute <i>put</i> method for writing a float
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes four bytes containing the given float value, in the
     * current byte order, into this buffer at the given index.  </p>
     *
     * @param  index
     *         The index at which the bytes will be written
     *
     * @param  value
     *         The float value to be written
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative
     *          or not smaller than the buffer's limit,
     *          minus three
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public Page setFloat(int index, float value){
        byteBuffer.putFloat(index, value);
        return this;
    }

    /**
     * Relative <i>get</i> method for reading a float value.
     *
     * <p> Reads the next four bytes at this buffer's current position,
     * composing them into a float value according to the current byte order,
     * and then increments the position by four.  </p>
     *
     * @return  The float value at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than four bytes
     *          remaining in this buffer
     */
    public float getFloat(){
        return byteBuffer.getFloat();
    }

    /**
     * Absolute <i>get</i> method for reading a float value.
     *
     * <p> Reads four bytes at the given index, composing them into a
     * float value according to the current byte order.  </p>
     *
     * @param  index
     *         The index from which the bytes will be read
     *
     * @return  The float value at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative
     *          or not smaller than the buffer's limit,
     *          minus three
     */
    public float getFloat(int index){
        return byteBuffer.getFloat(index);
    }

    /**
     * Relative <i>put</i> method for writing a long
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes eight bytes containing the given long value, in the
     * current byte order, into this buffer at the current position, and then
     * increments the position by eight.  </p>
     *
     * @param  value
     *         The long value to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there are fewer than eight bytes
     *          remaining in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public Page setLong(long value){
        byteBuffer.putLong(value);
        return this;
    }

    /**
     * Absolute <i>put</i> method for writing a long
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes eight bytes containing the given long value, in the
     * current byte order, into this buffer at the given index.  </p>
     *
     * @param  index
     *         The index at which the bytes will be written
     *
     * @param  value
     *         The long value to be written
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative
     *          or not smaller than the buffer's limit,
     *          minus seven
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public Page setLong(int index, long value){
        byteBuffer.putLong(index, value);
        return this;
    }

    /**
     * Relative <i>get</i> method for reading a long value.
     *
     * <p> Reads the next eight bytes at this buffer's current position,
     * composing them into a long value according to the current byte order,
     * and then increments the position by eight.  </p>
     *
     * @return  The long value at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than eight bytes
     *          remaining in this buffer
     */
    public long getLong(){
        return byteBuffer.getLong();
    }

    /**
     * Absolute <i>get</i> method for reading a long value.
     *
     * <p> Reads eight bytes at the given index, composing them into a
     * long value according to the current byte order.  </p>
     *
     * @param  index
     *         The index from which the bytes will be read
     *
     * @return  The long value at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative
     *          or not smaller than the buffer's limit,
     *          minus seven
     */
    public long getLong(int index){
        return byteBuffer.getLong(index);
    }

    /**
     * Relative <i>put</i> method for writing a short
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes two bytes containing the given short value, in the
     * current byte order, into this buffer at the current position, and then
     * increments the position by two.  </p>
     *
     * @param  value
     *         The short value to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there are fewer than two bytes
     *          remaining in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public Page setShort(short value){
        byteBuffer.putShort(value);
        return this;
    }

    /**
     * Relative <i>put</i> method for writing a short
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes two bytes containing the given short value, in the
     * current byte order, into this buffer at the current position, and then
     * increments the position by two.  </p>
     *
     * @param  value
     *         The short value to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there are fewer than two bytes
     *          remaining in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public Page setShort(int index, short value){
        byteBuffer.putShort(value);
        return this;
    }


    /**
     * Relative <i>get</i> method for reading a short value.
     *
     * <p> Reads the next two bytes at this buffer's current position,
     * composing them into a short value according to the current byte order,
     * and then increments the position by two.  </p>
     *
     * @return  The short value at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than two bytes
     *          remaining in this buffer
     */
    public short getShort(){
        return byteBuffer.getShort();
    }

    /**
     * Absolute <i>get</i> method for reading a short value.
     *
     * <p> Reads two bytes at the given index, composing them into a
     * short value according to the current byte order.  </p>
     *
     * @param  index
     *         The index from which the bytes will be read
     *
     * @return  The short value at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative
     *          or not smaller than the buffer's limit,
     *          minus one
     */
    public short getShort(int index){
        return byteBuffer.getShort(index);
    }

    /**
     * Relative <i>put</i> method for writing a char
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes two bytes containing the given char value, in the
     * current byte order, into this buffer at the current position, and then
     * increments the position by two.  </p>
     *
     * @param  value
     *         The char value to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If there are fewer than two bytes
     *          remaining in this buffer
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public Page setChar(char value){
        byteBuffer.putChar(value);
        return this;
    }

    /**
     * Absolute <i>put</i> method for writing a char
     * value&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes two bytes containing the given char value, in the
     * current byte order, into this buffer at the given index.  </p>
     *
     * @param  index
     *         The index at which the bytes will be written
     *
     * @param  value
     *         The char value to be written
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative
     *          or not smaller than the buffer's limit,
     *          minus one
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public Page setChar(int index, char value){
        byteBuffer.putChar(index, value);
        return this;
    }

    /**
     * Relative <i>get</i> method for reading a char value.
     *
     * <p> Reads the next two bytes at this buffer's current position,
     * composing them into a char value according to the current byte order,
     * and then increments the position by two.  </p>
     *
     * @return  The char value at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If there are fewer than two bytes
     *          remaining in this buffer
     */
    public char getChar(){
        return byteBuffer.getChar();
    }

    /**
     * Absolute <i>get</i> method for reading a char value.
     *
     * <p> Reads two bytes at the given index, composing them into a
     * char value according to the current byte order.  </p>
     *
     * @param  index
     *         The index from which the bytes will be read
     *
     * @return  The char value at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative
     *          or not smaller than the buffer's limit,
     *          minus one
     */
    public char getChar(int index){
        return byteBuffer.getChar(index);
    }

    /**
     * Relative <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes the given byte into this buffer at the current
     * position, and then increments the position. </p>
     *
     * @param  value
     *         The byte to be written
     *
     * @return  This buffer
     *
     * @throws  BufferOverflowException
     *          If this buffer's current position is not smaller than its limit
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public Page setByte(byte value){
        byteBuffer.put(value);
        return this;
    }


    /**
     * Absolute <i>put</i> method&nbsp;&nbsp;<i>(optional operation)</i>.
     *
     * <p> Writes the given byte into this buffer at the given
     * index. </p>
     *
     * @param  index
     *         The index at which the byte will be written
     *
     * @param  value
     *         The byte value to be written
     *
     * @return  This buffer
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative
     *          or not smaller than the buffer's limit
     *
     * @throws  ReadOnlyBufferException
     *          If this buffer is read-only
     */
    public Page setByte(int index, byte value){
        byteBuffer.put(index, value);
        return this;
    }

    /**
     * Relative <i>get</i> method.  Reads the byte at this buffer's
     * current position, and then increments the position.
     *
     * @return  The byte at the buffer's current position
     *
     * @throws  BufferUnderflowException
     *          If the buffer's current position is not smaller than its limit
     */
    public byte getByte(){
        return byteBuffer.get();
    }

    /**
     * Absolute <i>get</i> method.  Reads the byte at the given
     * index.
     *
     * @param  index
     *         The index from which the byte will be read
     *
     * @return  The byte at the given index
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code index} is negative
     *          or not smaller than the buffer's limit
     */
    public byte getByte(int index){
        return byteBuffer.get(index);
    }


    /**
     * The ByteBuffer class does not have methods to read and write strings, so
     * Page chooses to write string values as blobs.
     * The Java String class has a method getBytes, which converts a string into a byte array.
     * it also has a constructor that converts the byte array back to a string. Thus, Page’s setString method calls
     * getBytes to convert the string to bytes and then writes those bytes as a blob.
     * For example, tne string stored in buffer like this: [Int, byte1,byte2, byte3], the string is composed of these bytes [byte1, byte2, byte3] and the Int in this case is 3
     *
     * @param index The index at which the bytes will be written, but in this case,
     *              we put in it an integer number that represent the string bytes length (string bytes length),
     *              so we can convert back these bytes to real string value.
     * @param value the string that will be stored in byteBuffer object.
     * @apiNote The byteBuffer position will be changed, so take care of setting the appropriate index, otherwise unpredictable data will be obtained.
     */
    public void setString(int index, String value) {
        byte[] stringBytes = value.getBytes(CHARSET);
        byteBuffer.position(index);
        byteBuffer.putInt(stringBytes.length);
        byteBuffer.put(stringBytes, 0, stringBytes.length);
    }

    /**
     * Reads a blob from the byte buffer and then converts the bytes to a string
     *
     * @param index The index at which the bytes will be read, but in this case,
     *              we get from it an integer number that represent the string bytes length (string bytes length),
     *              so we can convert back these bytes to real string value by calculating the bytes length to convert them back.
     * @return String (real text)
     * @apiNote The byteBuffer position will be changed, so take care of setting the appropriate index, otherwise unpredictable data will be obtained.
     */
    public String getString(int index) {
        byteBuffer.position(index);
        int strLength = byteBuffer.getInt();
        byte[] stringBytes = new byte[strLength];
        byteBuffer.get(stringBytes, 0, stringBytes.length);

        return new String(stringBytes, CHARSET);
    }

    /**
     * add random bytes to the buffer, the first bytes before inserting the actual bytes will be an integer that represents the
     * bytes length in byteBuffer object.
     * The position of this buffer is then incremented by length + index (integer bytes).
     *
     * @param index the index at which we start inserting bytes.
     * @param bytes the actual data that will be stored.
     */
    public Page setBytes(int index, byte[] bytes) {
        byteBuffer.position(index).putInt(bytes.length).put(bytes, 0, bytes.length);
        return this;
    }

    /**
     * Get bytes from a specific location by index.
     * The position of this buffer is then incremented by index + length.
     *
     * @param index the index at which the bytes length is stored as an integer.
     * @return the bytes that has an equal length to the integer that obtained from the index position.
     */
    public byte[] getBytes(int index) {
        byteBuffer.position(index);
        int bytesLength = byteBuffer.getInt();
        byte[] bytes = new byte[bytesLength];
        byteBuffer.get(bytes , 0 ,bytesLength);
        return bytes;
    }


    /**
     * Get page's content, the content is represented as a sequence of bytes stored in byteBuffer object that
     * acts as array of bytes.
     *
     * @return page's byteBuffer object.
     */
    ByteBuffer contents() {
        byteBuffer.position(0);
        return byteBuffer;
    }

    public int getStringBytesLength(String value) {
        return value.getBytes(CHARSET).length;
    }


    /**
     * Get current page position
     * @return position of this page (buffer position)
     */
    public int position(){
        return byteBuffer.position();
    }

    /**
     * Sets this page's position. If the mark is defined and larger than the new position then it is discarded.
     * @param index The new position value; must be non-negative and no larger than the current limit
     * @return This page
     */
    public Page position(int index){
        byteBuffer.position(index);
        return this;
    }


    /**
     * Tells whether there are any elements between the current position and the limit.
     * @return true if, and only if, there is at least one element remaining in this buffer
     */
    public boolean hasRemaining() {
        return byteBuffer.hasRemaining();
    }
}