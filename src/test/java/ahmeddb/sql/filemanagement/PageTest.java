package ahmeddb.sql.filemanagement;

import ahmeddb.sql.configuration.DataSourceConfigProvider;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for page class
 */
class PageTest {

    @Test
    void getInt() {
        //arrange
        Page page = new Page();
        page.setInt(100,19);
        page.setInt(104,2000);

        //act & assert
        assertEquals(19,page.getInt(100));
        assertEquals(2000,page.getInt(104));

    }

    @Test
    void setInt() {
        //arrange
        Page page = new Page();

        //act
        page.setInt(100,19);
        page.setInt(104,2000);

        //assert
        assertEquals(19,page.getInt(100));
        assertEquals(2000,page.getInt(104));
    }

    @Test
    void setString() {

        //arrange
        Page page = new Page();
        String str = "Ahmed";
        int byteBufferIndex = 0;
        int stringBytesLength = page.getStringBytesLength(str);

        //act
        page.setString(byteBufferIndex,str);
        page.setString(byteBufferIndex + Integer.BYTES + stringBytesLength,"test");
        int actualStringBytesLength = page.getInt(byteBufferIndex);
        String actualString = page.getString(byteBufferIndex);
        String actualString2 = page.getString(byteBufferIndex + Integer.BYTES + stringBytesLength);


        //assert
        assertEquals(stringBytesLength,actualStringBytesLength);
        assertEquals(str,actualString);
        assertEquals("test",actualString2);

    }

    @Test
    void getString() {
        //arrange
        Page page = new Page();
        String str = "Ahmed";
        int byteBufferIndex = 0;
        int stringBytesLength = page.getStringBytesLength(str);

        //act
        page.setString(byteBufferIndex,str);
        page.setString(byteBufferIndex + Integer.BYTES + stringBytesLength,"test");
        int actualStringBytesLength = page.getInt(byteBufferIndex);
        String actualString = page.getString(byteBufferIndex);
        String actualString2 = page.getString(byteBufferIndex + Integer.BYTES + stringBytesLength);


        //assert
        assertEquals(stringBytesLength,actualStringBytesLength);
        assertEquals(str,actualString);
        assertEquals("test",actualString2);
    }

    @Test
    void getByteBuffer() {
        //arrange
        Page page = new Page();
        Page page2 = new Page(new byte[10]);
        int dbBlockSize = DataSourceConfigProvider.getDataSourceConfig().getBlockSize();

        //act
        int pageBlockSize = page.contents().capacity();

        //assert
        assertEquals(dbBlockSize, pageBlockSize);
        assertTrue(page.contents().isDirect());
        assertFalse(page2.contents().isDirect());
        assertNotEquals(page,page2);
        assertFalse(page.contents().isReadOnly());
        assertFalse(page2.contents().isReadOnly());

    }
}