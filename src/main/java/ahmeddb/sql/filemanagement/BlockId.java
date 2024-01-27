package ahmeddb.sql.filemanagement;

/**
 * A BlockId object identifies a specific block by its file name and logical block
 * number. For example, the statement, BlockId blk = new BlockId("student.tbl", 23)
 * creates a reference to block 23 of the file student.tbl. The methods of
 * filename and number return its file name and block number.<br/>
 * The blockId helps us to identify the logical location of a block in a specific file.
 * For example, if the block size is 4096 bytes, that mean that the block number 1 (i.e: the second block) will start at the byte number 4097.
 * so by identifying the specific block location, we use the {@link Page} class to deals with content of some block,
 * either by reading from it or writing to it.
 * @param fileName The name of the file which the blockId object belongs.
 * @param number The logical block number of the file that the blockId object belongs.
 */
public record BlockId (String fileName, long number) {}
