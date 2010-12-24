package org.randomcoder.io;

import java.io.*;
import java.nio.CharBuffer;
import java.util.*;

import junit.framework.TestCase;

public class SequenceReaderTest extends TestCase
{
	private static final String TEXT1 = "Reader Number One";
	private static final String TEXT2 = "Reader Number Two";
	private static final String TEXT3 = "Reader Number Three"; 
	
	private static final String TEXT_COMBINED = TEXT1 + TEXT2 + TEXT3;
	
	private SequenceReader seqReader;
	private Reader stdReader;
	private Reader reader1;
	private Reader reader2;
	private Reader reader3; 
	
	@Override
	public void setUp() throws Exception
	{
		stdReader = new StringReader(TEXT_COMBINED);
		
		reader1 = new StringReader(TEXT1);
		reader2 = new StringReader(TEXT2);
		reader3 = new StringReader(TEXT3);
		seqReader = new SequenceReader(reader1, reader2, reader3);
	}

	@Override
	public void tearDown() throws Exception
	{
		try { reader1.close(); } catch (Exception e) {}
		try { reader2.close(); } catch (Exception e) {}
		try { reader3.close(); } catch (Exception e) {}
		try { stdReader.close(); } catch (Exception e) {}
		try { seqReader.close(); } catch (Exception e) {}
		reader1 = null;
		reader2 = null;
		reader3 = null;
		stdReader = null;
		seqReader = null;
	}
	
	public void testRead() throws Exception
	{
		int c1 = -1;
		int c2 = -1;
		
		do
		{
			c1 = stdReader.read();
			c2 = seqReader.read();			
			if (c1 > 0) assertEquals(c1, c2);
		} while (c1 >= 0);
	}

	public void testReadCharArray() throws Exception
	{
		int c;
		
		char[] buf1 = new char[TEXT_COMBINED.length()];
		char[] buf2 = new char[TEXT_COMBINED.length()];
		
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		
		// read standard
		int t1 = 0;		
		do 
		{
			c = stdReader.read(buf1);
			if (c > 0)
			{
				t1 += c;
				sb1.append(buf1, 0, c);
			}
		}
		while (c >= 0);
		
		int t2 = 0;
		do 
		{
			c = seqReader.read(buf2);
			if (c > 0)
			{
				t2 += c;
				sb2.append(buf2, 0, c);
			}
		}
		while (c >= 0);
		
		assertEquals("Length doesn't match buffer", buf1.length, t1);
		assertEquals("Wrong length", t1, t2);
		
		String s1 = sb1.toString();
		String s2 = sb2.toString();
		
		assertEquals("Results don't match", s1, s2);
	}

	public void testReadCharArrayIntInt() throws Exception
	{
		int c;
		
		char[] buf1 = new char[TEXT_COMBINED.length()];
		char[] buf2 = new char[TEXT_COMBINED.length()];
		
		int t1 = 0;
		do 
		{
			if (buf1.length > t1)
			{
				c = stdReader.read(buf1, t1, buf1.length - t1);
				if (c > 0)
				{
					t1 += c;
				}
			}
			else // handle the last char
			{
				c = stdReader.read();
			}
		}
		while (c >= 0);

		int t2 = 0;
		do 
		{
			if (buf2.length > t2)
			{
				c = seqReader.read(buf2, t2, buf2.length - t2);
				if (c > 0)
				{
					t2 += c;
				}
			}
			else // handle the last char
			{
				c = seqReader.read();
			}
		}
		while (c >= 0);

		assertEquals("Length doesn't match buffer", buf1.length, t1);
		assertEquals("Wrong length", t1, t2);
		
		String s1 = new String(buf1);
		String s2 = new String(buf2);
		
		assertEquals("Results don't match", s1, s2);
	}

	public void testSkip() throws Exception
	{
		// skip all but last 5 chars
		long skip = TEXT_COMBINED.length() - 5;
		
		while (skip > 0)
		{
			long actual = seqReader.skip(skip);
			assertTrue("skip returned negative", actual >= 0);
			assertTrue("skip returned too many", actual <= skip);
			skip -= actual;
		}
		
		// read 3 chars
		assertEquals('T', (char) seqReader.read());
		assertEquals('h', (char) seqReader.read());
		assertEquals('r', (char) seqReader.read());
		
		// skip last 2 chars
		assertEquals(1L, seqReader.skip(1));
		assertEquals(1L, seqReader.skip(1));
		
		// skip past end
		assertEquals(0L, seqReader.skip(1));
		
	}

	public void testReady() throws Exception 
	{
		int c;		
		do
		{
			assertTrue("Reader reports not ready during stream", seqReader.ready());
			c = seqReader.read();
		} while (c >= 0);
		
		// no longer ready
		assertFalse("Reader reports ready at end of stream", seqReader.ready());
	}

	public void testMarkSupported()
	{
		assertFalse("Mark shouldn't be supported", seqReader.markSupported());
	}

	public void testMark() throws Exception
	{
		try
		{
			seqReader.mark(1);
			fail("IOException expected");
		}
		catch (IOException e)
		{
			// pass
		}
	}

	public void testReset() throws Exception
	{
		try
		{
			seqReader.reset();
			fail("IOException expected");
		}
		catch (IOException e)
		{
			// pass
		}
	}
	
	public void testClose() throws Exception
	{
		seqReader.close();
		
		try
		{
			reader1.read();
			fail("Reader 1 didn't throw an exception");
		}
		catch (IOException e) {}
		try
		{
			reader2.read();
			fail("Reader 2 didn't throw an exception");
		}
		catch (IOException e) {}
		try
		{
			reader3.read();
			fail("Reader 3 didn't throw an exception");
		}
		catch (IOException e) {}
	}

	public void testReadCharBuffer() throws Exception
	{
		CharBuffer cb = CharBuffer.allocate(TEXT_COMBINED.length() + 1);
		int c;
		int len = 0;
		do
		{
			c = seqReader.read(cb);
			if (c > 0) len += c;
		}
		while (c >= 0);
		
		// try to read past end
		assertEquals("Input past end", -1, seqReader.read(cb));
		
		// check length
		assertEquals("Wrong length", TEXT_COMBINED.length(), len);
		
		char[] buf = new char[len];
		
		cb.flip();
		cb.get(buf);
		
		String s1 = new String(buf);
		
		assertEquals("String invalid", TEXT_COMBINED, s1);
	}

	public void testListReader() throws IOException
	{
		List<Reader> readers = new ArrayList<Reader>();
		readers.add(reader1);
		readers.add(reader2);
		readers.add(reader3);
		
		SequenceReader listReader = new SequenceReader(readers);
		
		
		StringWriter writer = new StringWriter();
		
		int c;
		do
		{
			c = listReader.read();
			if (c >= 0) writer.append((char) c);
		}
		while (c > 0);
		
		writer.close();
		writer.close();
		
		assertEquals("Wrong buffer in reader list", TEXT_COMBINED, writer.getBuffer().toString());
	}
	
	@SuppressWarnings("unchecked")
	public void testNullListConstructor()
	{
		try
		{
			new SequenceReader((List) null);
			fail("NullPointerException expected");
		}
		catch (NullPointerException e)
		{
			// pass
		}
	}

	public void testNullListElementConstructor()
	{
		List<Reader> list = new ArrayList<Reader>();
		list.add(null);
		list.add(reader1);

		try
		{
			new SequenceReader(list);
			fail("NullPointerException expected");
		}
		catch (NullPointerException e)
		{
			// pass
		}
	}

	public void testNullReaderConstructor()
	{
		try
		{
			new SequenceReader((Reader) null);
			fail("NullPointerException expected");
		} 
		catch (NullPointerException e)
		{
			// pass
		}
	}
	
	public void testNullReaderListConstructor()
	{
		try
		{
			new SequenceReader((Reader) null, reader1);
			fail("NullPointerException expected");
		}
		catch (NullPointerException e)
		{
			// pass
		}
	}
	
	public void testNullSecondaryReader() throws IOException
	{
		try
		{
			SequenceReader reader = new SequenceReader(reader1, null);
			
			int c;
			do
			{
				c = reader.read();			
			}
			while (c >= 0);
			
			fail("NullPointerException expected");
		}
		catch (NullPointerException e)
		{
			// pass
		}
	}
	
	public void testSkipZero() throws IOException
	{
		assertEquals(0L, seqReader.skip(0));
	}
	
	public void testSkipNegative() throws IOException
	{
		try
		{
			seqReader.skip(-1);
			fail("IllegalArgumentException expected");
		}
		catch (IllegalArgumentException e)
		{
			// pass
		}
	}
	
	public void testIndexOutOfBoundsRead() throws IOException
	{
		try
		{
			seqReader.read(new char[10], -1, 1);
			fail("IndexOutOfBoundsException expected");
		}
		catch (IndexOutOfBoundsException e)
		{
			// pass
		}
	}
	
	public void testReadZeroChars() throws IOException
	{
		assertEquals(0, seqReader.read(new char[10], 0, 0));
	}
}
