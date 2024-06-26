package utils;

import java.io.Serializable;

/** Simple byte array (or a kind of stream) that grows when data is added.
You can add/read basic types and byte arrays. short, int and long are stored
as big or little endian. The default is LITTLE_ENDIAN.

Use this to convert Java types to/from C-structs.

If you decode/encode named values (properties) to/from C-structs
use one of the bincoders.

@see bincoder.E_BinaryCoder
@author na
 **/
public class TransArr implements Cloneable, Serializable {
    // Remember to update clone() if you add any new members that are references!

	private static final long serialVersionUID = 1L;
	public static final byte BIG_ENDIAN = 1;
    public static final byte LITTLE_ENDIAN = 2;
    protected final int MIN_INC = 10;
    protected byte[] _arr = null;
    protected int _length = 0;
    protected int _increment = MIN_INC;
    protected int _endian = LITTLE_ENDIAN;
    protected int _position = 0;
    
 
    
//    public void debug() {
//        db.println("message  terminal >> host");
//        String hexString;
//        for (int i = 0; i < _length; i++) {
//            if ((i % 16) == 0) {
//                System.out.println();
//            }
//            hexString = Integer.toHexString((_arr[i] & 0x000000FF));
//            hexString = (hexString.length() > 1) 
//                         ? (" " + hexString)
//                         : (" 0" + hexString);
//            System.out.print(" " + hexString);
//        }
//        System.out.println();
//    }
    
    
    /** Start of with a null array **/
    public TransArr() {
        // all set with defaults
    }

    /** Start of with storage capacity len **/
    public TransArr(int len) {
        _arr = new byte[len];
    }

    /** Start of with storage capacity len and increment inc **/
    public TransArr(int len, int inc) {
        _arr = new byte[len];
        if (inc > MIN_INC) {
            _increment = inc;
        }
    }

    /** Start of with an array containing the given byte array **/
    public TransArr(byte[] init) {
        add(init);
    }

    /** Start of with an array containing the given byte array **/
    public TransArr(byte[] init, int offset, int length) {
        add(init, offset, length);
    }

    /** Start of with an array containing the given byte array **/
	public TransArr(TransArr darr) {
        add(darr.getBytes());
    }

    /** Set how integer types are stored internally (big or little endian)
     **/
    public void setEndian(int type) {
        _endian = type;
    }

    public int getEndian() {
        return _endian;
    }

    /** Return length of data **/
    public int length() {
        return (_length);
    }

    /** Add a char to the end of current data. 
     * @deprecated for use only internally inside TransArr class. Outside use add_X() instead.
     */
    public void add(char val) {
        if (_arr == null) {
            _lengthen();
        } else if (_length + 1 >= _arr.length) {
            _lengthen();
        }
        _arr[_length++] = (byte) Character.getNumericValue(val);
    }

    /** Add a byte to the end of current data. 
     * @deprecated for use only internally inside TransArr class. Outside use add_X() instead.
     */
    public void add(byte val) {
        if (_arr == null) {
            _lengthen();
        } else if (_length + 1 >= _arr.length) {
            _lengthen();
        }
        _arr[_length++] = val;
    }

    /** Add a byte to the end of current data. **/
    public void add_1(int val) {
        if (_arr == null) {
            _lengthen();
        } else if (_length + 1 >= _arr.length) {
            _lengthen();
        }
        _arr[_length++] = (byte)(val & 0xFF);
    }
    
    /** Add a byte taken from int to the end of current data. **/
    public void add_2(int val) {
        byte shorty[] = new byte[2];

        shorty[0] |= val & 0xFF;
        shorty[1] |= (val >>> 8) & 0xFF;
        shorty = _massage(shorty);
        add(shorty);
    }
    
    /** Add 3 bytes taken from int to the end of current data. **/
    public void add_3(int val) {
        byte intArr[] = new byte[3];

        for (short i = 0; i < 3; i++) {
            intArr[i] |= val & 0xFF;
            val >>>= 8;
        }
        intArr = _massage(intArr);
        add(intArr);
    }

    /** Add an int taken from long to the end of current data. **/
    public void add_4(int val) {
//        System.out.println("add_4 : val = " + val);
//        System.out.println("  val hex = " + Long.toHexString(val));

        byte intArr[] = new byte[4];

        for (short i = 0; i < 4; i++) {
            intArr[i] = (byte)(val & 0xFF);
            val >>>= 8;
        }
        
//        System.out.print("- arr = ");
//        for (int i=0; i<4; i++)
//            System.out.print(Integer.toHexString(intArr[i] & 0xFF) + " ");
//        
//        System.out.println("\n");
        intArr = _massage(intArr);

//        System.out.print("+ arr = ");
//        for (int i=0; i<4; i++)
//            System.out.print(Integer.toHexString(intArr[i] & 0xFF) + " ");
//        System.out.println("\n");

        add(intArr);
    }
    
    /** Add a long to the end of current data. **/
    public void add_8(long val) {
//        System.out.println("add_8 : val = " + val);
//        System.out.println("  val hex = " + Long.toHexString(val));

        byte intArr[] = new byte[8];

        for (short i = 0; i < 8; i++) {
            intArr[i] = (byte)(val & 0xFF);
            val >>>= 8;
        }
        
//        System.out.print("- arr = ");
//        for (int i=0; i<8; i++)
//            System.out.print(Integer.toHexString(intArr[i] & 0xFF) + " ");
//        
//        System.out.println("\n");
        intArr = _massage(intArr);

//        System.out.print("+ arr = ");
//        for (int i=0; i<8; i++)
//            System.out.print(Integer.toHexString(intArr[i] & 0xFF) + " ");
//        System.out.println("\n");

        add(intArr);
    }
    
    /** Add a short to the end of current data.
     * @deprecated for use only internally inside TransArr class. Outside use add_X() instead.
     */
    public void add(short val) {
        byte shorty[] = new byte[2];

        shorty[0] |= val & 0xFF;
        shorty[1] |= (val >>> 8) & 0xFF;
        shorty = _massage(shorty);
        add(shorty);
    }

    /** Add an int to the end of current data.
     * @deprecated for use only internally inside TransArr class. Outside use add_X() instead.
     */
    public void add(int val) {
        byte intArr[] = new byte[4];

        for (short i = 0; i < 4; i++) {
            intArr[i] |= val & 0xFF;
            val >>>= 8;
        }
        intArr = _massage(intArr);
        add(intArr);
    }

    /** Add a long to the end of current data.
     * @deprecated for use only internally inside TransArr class. Outside use add_X() instead.
     */
    public void add(long val) {
        byte longArr[] = new byte[8];

        for (int i = 0; i < 8; i++) {
            longArr[i] |= val & 0xFF;
            val >>>= 8;
        }
        longArr = _massage(longArr);
        add(longArr);
    }

    /** Add an integer of given byte length (this came in late,
    so it is probably not used in all places it should).
    Works for 1-7 bytes, array out of bounds exception for more.
     **/
    public void addUnsigned(long val, int len) {
        byte[] arr = new byte[Math.min(len, 7)];

        for (int i = 0; i < len; i++) {
            arr[i] |= val & 0xFF;
            val >>>= 8;
        }
        arr = _massage(arr);
        add(arr);
    }

    /** Add a byte array to the end of current data. **/
    public void add(byte[] addArr) {
    	add(addArr, 0, addArr.length);
    }
    
    /** Add a byte array to the end of current data. **/
    public void add(byte[] addArr, int offset, int length) {
        if (_arr == null) {
            _arr = new byte[length + _increment];
            _length = 0;
        } else if (_length + length >= _arr.length) {
            _lengthen(
                    length - (_arr.length - _length) + _increment);
        }
        System.arraycopy(addArr, offset, _arr, _length, length);
        _length += length;
    }

    /** Add data of another TransArr to the end of current data. **/
    public void add(TransArr addDarr) {
        if (addDarr._length == 0) {
            return;   // BUG exception?

        }
        add(addDarr.getBytes());
    }

    /** Add a string to the end of current data.  The string is converted
    into bytes using getBytes() and is then null terminated with a
    zero byte.
     **/
    public void add(String str) {
        byte strArr[] = new byte[str.length() + 1];

        System.arraycopy(str.getBytes(), 0, strArr, 0, str.length());
        strArr[str.length()] = (byte) 0;
        add(strArr);
    }

    /** Insert byte array at specified offset **/
    public void insert(byte[] bytes, int offset) {
        // MISSING error check
        byte[] newArr = new byte[_length + bytes.length + _increment];

        if (offset == 0) {
            System.arraycopy(bytes, 0, newArr, 0, bytes.length);
            System.arraycopy(_arr, 0, newArr, bytes.length, _length);
        } else {
            System.arraycopy(_arr, 0, newArr, 0, offset);
            System.arraycopy(bytes, 0, newArr, offset, bytes.length);
            System.arraycopy(_arr, offset, newArr, offset + bytes.length,
                _length - offset);
        }

        _arr = newArr;
        _length += bytes.length;
    }
    
    /** Insert 2 byte value at specified offset **/
    public void insert_2(int val, int offset) {
        // MISSING error check
    	byte bytes[] = new byte[2];
    	bytes[0] |= val & 0xFF;
        bytes[1] |= (val >>> 8) & 0xFF;
        bytes = _massage(bytes);
        
        insert(bytes, offset);
    }

    /** Remove byte array from specified offset **/
    public TransArr remove(int offset, int length) {
        // MISSING error check
        byte[] newArr = new byte[_length - length + _increment];
        byte[] retArr = new byte[length];

        if (offset == 0) {
            System.arraycopy(_arr, 0, retArr, 0, length);
            System.arraycopy(_arr, length, newArr, 0, _length - length);
        } else {
            System.arraycopy(_arr, offset, retArr, 0, length);
            System.arraycopy(_arr, 0, newArr, 0, offset);
            System.arraycopy(_arr, offset + length, newArr, offset,
                _length - offset - length);
        }

        _arr = newArr;
        _length -= length;
        return (new TransArr(retArr));
    }
    
    /**
     * Removes all the data from this message.
     */
    public void removeAll(){
    	_arr = new byte[0];
        _length = 0;
        _position = 0;
    }

    /** Shorten actuall array to the length of data **/
    public void trim() {
        if (_arr == null) {
            return;
        }
        if (_arr.length == _length) {
            return;
        }
        byte newArr[] = new byte[_length];
        System.arraycopy(_arr, 0, newArr, 0, _length);
        _arr = newArr;
    }

    /** Returns a COPY of the array **/
    public byte[] getBytes() {
        byte[] retArr = new byte[_length];
        System.arraycopy(_arr, 0, retArr, 0, _length);
        return (retArr);
    }
    
    /** Returns a COPY of the array from the current position to the size **/
    public byte[] getBytesFromCurPos() {
        byte[] retArr = new byte[_length-_position];
        System.arraycopy(_arr, _position, retArr, 0, _length-_position);
        return (retArr);
    }    
    

    /** Position internal pointer at absolute index pos.  If 'pos' is beyond the
    end of the currently allocated space, the buffer is expanded and zero-filled.
    NOTE the position is defined after seek() and subsequent readxxx()'s, but
    more than that - well there are no guaranties.
     **/
    public void seek(int pos) {
        // expand the array is we seek beyond the end - FJM
        int arrLength = (_arr == null ? 0 : _arr.length);
        if (pos >= arrLength) {
            _lengthen(pos - arrLength + _increment);
        }
        if (pos > _length) {
            _length = pos;
        }
        _position = pos;
    }

    
    /** Return current position of the internal pointer as an absolute offset */
    public int getPosition() {
        return _position;
    }

    /**
     * @return true if at least one more byte is available to read; 
     * false otherwise.
     */
    public boolean isNext() {
        return (_position < _length);
    }
    
    /** Read a byte from the current position.
    Position advances to the next byte.
     **/
    public byte readByte() {
        byte b = _arr[_position++];
        if (_position > _length) {
            throw new ArrayIndexOutOfBoundsException(
                "readByte()");
        }
        return (b);
    }

    /** Read a short from the current position.
    Position advances to the next short.
     **/
    public short readShort() {
        short retValue = 0;

        byte[] shortArr = readBytes(2);
        shortArr = _massage(shortArr);
        retValue |= (shortArr[0]) & 0xFF;
        retValue |= (shortArr[1] << 8) & 0xFF00;
        return (retValue);
    }

    /** Write a short at the current position,
    overwriting what is already there.
    Position advances to the next short.
     **/
    public void writeShort(int saved_out_crc) {
        byte shorty[] = new byte[2];

        shorty[0] |= saved_out_crc & 0xFF;
        shorty[1] |= (saved_out_crc >>> 8) & 0xFF;
        shorty = _massage(shorty);
        writeBytes(shorty);
    }

    /** Read an int from the current position.
    Position advances to the next int.
     **/
    public int readInt() {
        int retValue = 0;

        byte[] intArr = readBytes(4);
        intArr = _massage(intArr);
        retValue |= intArr[0] & 0xFF;
        retValue |= (intArr[1] << 8) & 0xFF00;
        retValue |= (intArr[2] << 16) & 0xFF0000;
        retValue |= (intArr[3] << 24) & 0xFF000000;
        return (retValue);
    }

    /** Write an int at the current position,
    overwriting what is already there.
    Position advances to the next int.
     **/
    public void writeInt(int val) {
        byte intArr[] = new byte[4];

        intArr[0] |= val & 0xFF;
        intArr[1] |= (val >>> 8) & 0xFF;
        intArr[2] |= (val >>> 16) & 0xFF;
        intArr[3] |= (val >>> 24) & 0xFF;
        intArr = _massage(intArr);
        writeBytes(intArr);
    }

    /** Read a long from the current position.
    Position advances to the next long.
     **/
    public long readLong() {
        long retValue = 0;

        byte[] intArr = readBytes(8);
        intArr = _massage(intArr);

        retValue |= ((long) intArr[0]) & 0xFFL;
        retValue |= (((long) intArr[1]) << 8) & 0xFF00L;
        retValue |= (((long) intArr[2]) << 16) & 0xFF0000L;
        retValue |= (((long) intArr[3]) << 24) & 0xFF000000L;
        retValue |= (((long) intArr[4]) << 32) & 0xFF00000000L;
        retValue |= (((long) intArr[5]) << 40) & 0xFF0000000000L;
        retValue |= (((long) intArr[6]) << 48) & 0xFF000000000000L;
        retValue |= (((long) intArr[7]) << 56) & 0xFF00000000000000L;

        return (retValue);
    }

    /** Write an long at the current position,
    overwriting what is already there.
    Position advances to the next long.
     **/
    public void writeLong(long val) {
        byte intArr[] = new byte[8];

        intArr[0] |= val & 0xFF;
        intArr[1] |= (val >>> 8) & 0xFF;
        intArr[2] |= (val >>> 16) & 0xFF;
        intArr[3] |= (val >>> 24) & 0xFF;
        intArr[4] |= (val >>> 32) & 0xFF;
        intArr[5] |= (val >>> 40) & 0xFF;
        intArr[6] |= (val >>> 48) & 0xFF;
        intArr[7] |= (val >>> 56) & 0xFF;
        intArr = _massage(intArr);
        writeBytes(intArr);
    }
    
    /** Read an integer of given byte length (this came in late,
    so it is probably not used in all places it should).
    Works for 1-7 bytes, array out of bounds exception for more.
    
     **/
    public long readUnsigned(int len) {
        long retValue = 0;
        long andByte = 0xFF;

        byte[] arr = readBytes(Math.min(len, 7));
        arr = _massage(arr);

        for (int i = 0, shift = 0; i < len; i++, shift += 8) {
            retValue |= (((long) arr[i]) << shift) & andByte;
            andByte <<= 8;
        }

        return (retValue);
    }

    /** 
     * Read unsigned byte and return as short 
     **/
    public short read_1() {
        short retValue = 0;

        byte[] shortArr = readBytes(1);
        shortArr = _massage(shortArr);
        retValue |= (shortArr[0]) & 0xFF;
        return (retValue);
    }
    
    /**
     *  Read signed byte and return as short
     *  @deprecated not tested 
     **/
    public short readSigned_1() {
        short retValue = 0;

        byte[] shortArr = readBytes(1);
        shortArr = _massage(shortArr);
        retValue |= shortArr[0];
        return (retValue);
    }

    /** 
     * Reads 2 Bytes (unsigned) from the current position.
     **/
    public int read_2() {
        int retValue = 0;

        byte[] intArr = readBytes(2);
        intArr = _massage(intArr);
        retValue |= intArr[0] & 0xFF;
        retValue |= (intArr[1] << 8) & 0xFF00;
        return (retValue);
    }

    /** Reads 2 Bytes (signed) from the current position.
     * @deprecated not tested
     **/
    public int readSigned_2() {
       int retValue = 0;

       byte[] intArr = readBytes(2);
       intArr = _massage(intArr);
       retValue = intArr[1];
       retValue <<= 8;
       retValue |= (intArr[0] & 0x00FF);
       return (retValue);
   }

    /** 
     * Reads 3 Bytes from the current position and returns unsigned integer.
     **/
    public int read_3() {
        int retValue = 0;

        byte[] intArr = readBytes(3);
        intArr = _massage(intArr);
        retValue |= intArr[0] & 0xFF;
        retValue |= (intArr[1] << 8) & 0xFF00;
        retValue |= (intArr[2] << 16) & 0xFF0000;        
        return (retValue);
    }
    
    /** 
     * Reads 3 Bytes from the current position and returns signed integer.
     * @deprecated not tested
     **/
    public int readSigned_3() {
        int retValue = 0;

        byte[] intArr = readBytes(3);
        intArr = _massage(intArr);
        retValue = intArr[2];
        retValue <<= 16;
        retValue |= (intArr[1] << 8) & 0x0000FF00;
        retValue |= intArr[0] & 0x000000FF;
        return (retValue);
    }

    /** 
     * Read 4 bytes from the current position and returns an unsigned long.
     **/
    public long read_4() {
        long retValue = 0L;

        byte[] intArr = readBytes(4);
        intArr = _massage(intArr);

        retValue |= ((long) intArr[0]) & 0xFFL;
        retValue |= (((long) intArr[1]) << 8) & 0xFF00L;
        retValue |= (((long) intArr[2]) << 16) & 0xFF0000L;
        retValue |= (((long) intArr[3]) << 24) & 0xFF000000L;
        
        return (retValue);
    }
    
    /** 
     * Read 4 bytes from the current position and returns an signed long.
     * @deprecated not tested
     **/
    public long readSigned_4() {
        long retValue = 0L;

        byte[] intArr = readBytes(4);
        intArr = _massage(intArr);

        retValue = ((long) intArr[3]) << 24;
        retValue |= (((long) intArr[2]) << 16) & 0xFF0000L;
        retValue |= (((long) intArr[1]) << 8) & 0xFF00L;
        retValue |= ((long) intArr[0]) & 0xFFL;
        
        return (retValue);
    }
    
    /** Read a long from the current position.
    Position advances to the next long.
     **/
    public long read_8() {
        long retValue = 0;

        byte[] intArr = readBytes(8);
        intArr = _massage(intArr);

        retValue |= ((long) intArr[0]) & 0xFFL;
        retValue |= (((long) intArr[1]) << 8) & 0xFF00L;
        retValue |= (((long) intArr[2]) << 16) & 0xFF0000L;
        retValue |= (((long) intArr[3]) << 24) & 0xFF000000L;
        retValue |= (((long) intArr[4]) << 32) & 0xFF00000000L;
        retValue |= (((long) intArr[5]) << 40) & 0xFF0000000000L;
        retValue |= (((long) intArr[6]) << 48) & 0xFF000000000000L;
        retValue |= (((long) intArr[7]) << 56) & 0xFF00000000000000L;
        
        return (retValue);
    }

 
    
    /** Read a byte array from the current position.
    Position advances to next available position.
     **/
    public byte[] readBytes(int len) {
        byte[] retArr = new byte[len];
        System.arraycopy(_arr, _position, retArr, 0, len);
        _position += len;
        if (_position > _length) {
            throw new ArrayIndexOutOfBoundsException(
                "readByte()");
        }
        return (retArr);
    }

    /** Write a byte from the current position, overwriting
    the current values stored there.  Position advances to next
    available position.
     **/
    public void writeByte(byte b) {
        // If the write will extend off the end of the current
        // _arr, treat as a no op.

        if (_arr == null) {
            return;
        }
        if (_position + 1 > _arr.length) {
            return;
        }
        _arr[_position] = b;
        _position++;
    }

    /**
     * Write a byte at the current position, overwriting what is already there.
     * Position advances by 1.
     * @param val
     */
    public void write_1(short val) {
        writeByte((byte) val);
    }
    
    /**
     * Write a short at the current position, overwriting what is already there.
     * Position advances by 2.
     * @param val
     */
    public void write_2(int val) {
        writeShort(val);
    }
    
    /**
     * Write an int at the current position, overwriting what is already there.
     * Position advances by 4.
     * @param val
     */
    public void write_4(long val) {
        writeInt((int) val);
    }
    
    /**
     * Write a long at the current position, overwriting what is already there.
     * Position advances by 8.
     * @param val
     */
    public void write_8(long val) {
        writeLong(val);
    }
    
    /** Write a byte array from the current position, overwriting
    the current values stored there.  Position advances to next
    available position.
     **/
    public void writeBytes(byte[] writeArr) {
        // If the write will extend off the end of the current
        // _arr, treat as a no op.

        if (writeArr.length == 0 || _arr == null) {
            return;
        }
        if (_position + writeArr.length - 1 >= _arr.length) {
            return;
        }
        System.arraycopy(writeArr, 0, _arr, _position, writeArr.length);
        _position += writeArr.length;
    }

    /** Read a string from the current position.
    Position advances to the next long.
     **/
    public String readStr() {
        int len = _position;

        while (_arr[len++] != 0) {
            if (len > 100000) // probably an error if it's bigger than this
            {
                throw new ArrayIndexOutOfBoundsException("Can't find a null " + len);
            }
        }

        len -= _position;
        byte[] retArr = readBytes(len);

        return (new String(retArr, 0, len - 1));
    }
    
    /** Lengthen the internal byte storage. You might want to do this if you
    dynamically decide to add, say a lot of bytes, one at a time and you
    know how many you will add.
     **/
    private void _lengthen(int inc) {
        byte newArr[];
        if (_arr != null) {
            newArr = new byte[_arr.length + inc];
            System.arraycopy(_arr, 0, newArr, 0, _length);
        } else {
            newArr = new byte[inc];
            _length = 0;
        }

        _arr = newArr;
    }

    /** Lengthen the internal byte storage by _increment **/
    private void _lengthen() {
        _lengthen(_increment);
    }

    /**
    Change byte order of integer data stored in an array, according to
    current _endian setting.
     **/
    private byte[] _massage(byte arr[]) {
        if (_endian == BIG_ENDIAN) {
            byte newArr[] = new byte[arr.length];
            for (int i = 0, j = arr.length - 1; j >= 0; j--, i++) {
                newArr[i] = arr[j];
            }
            arr = newArr;
        }

        return (arr);
    }

    public String toString() {
        String hex_chars = "0123456789ABCDEF";
        StringBuffer sbuff;
        int length = 0;
        byte b;

        if (_arr != null) {
            //length = _arr.length;
            length = _length;
        }
        sbuff = new StringBuffer(4 * length);

        sbuff.append("[");
        sbuff.append("length=");
        sbuff.append(length);
        sbuff.append(" bytes={");

        for (int i = 0; i < length; i++) {
            b = _arr[i];
            if (i != 0) {
                sbuff.append(" ");
            }
            sbuff.append(hex_chars.charAt((b >>> 4) & 0x0F));
            sbuff.append(hex_chars.charAt(b & 0x0F));
        }

        sbuff.append("}]");

        return sbuff.toString();
    }

    /**
    This method is needed to properly implement Cloneable interface.
    Remember to update it if you add any new members that are references!
    
    @return  a clone of this trans array.
     **/
    public synchronized Object clone() {
        try {
            TransArr transArr = (TransArr) super.clone();

            // Clone any members that are references
            if (_arr != null) {
                int arrLen = _arr.length;
                transArr._arr = new byte[arrLen];
                System.arraycopy(_arr, 0, transArr._arr, 0, arrLen);
            }

            return transArr;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

}
