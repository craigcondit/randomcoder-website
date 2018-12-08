package org.randomcoder.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Chaining {@link Reader} implementation.
 */
public class SequenceReader extends Reader {
  private final Iterator<Reader> readerIterator;
  private Reader currentReader;

  /**
   * Creates a new {@code SequenceReader} which concatenates the given
   * {@code Reader} objects.
   *
   * @param readers List of readers to chain
   */
  public SequenceReader(List<Reader> readers) {
    readerIterator = readers.iterator();
    if (readerIterator.hasNext()) {
      currentReader = readerIterator.next();
      if (currentReader == null) {
        throw new NullPointerException();
      }
    }
  }

  /**
   * Creates a new {@code SequenceReader} which concatenates the given
   * {@code Reader} objects.
   *
   * @param readers Array of readers to chain
   */
  public SequenceReader(Reader... readers) {
    List<Reader> readerList = new ArrayList<>(readers.length);
    for (Reader reader : readers) {
      readerList.add(reader);
    }
    readerIterator = readerList.iterator();
    if (readerIterator.hasNext()) {
      currentReader = readerIterator.next();
      if (currentReader == null) {
        throw new NullPointerException();
      }
    }
  }

  /**
   * Reads characters into a buffer.
   *
   * @param cbuf character buffer
   * @param off  offset into buffer
   * @param len  number of characters to read
   * @return number of characters read, or -1 on EOF
   * @throws IOException if an error occurs
   */
  @Override public int read(char[] cbuf, int off, int len) throws IOException {
    if (currentReader == null) {
      return -1; // at end of all streams
    }

    // off, len must be > 0, sum must be <= length
    if (off < 0 || len < 0 || off + len > cbuf.length) {
      throw new IndexOutOfBoundsException();
    }

    // special case -- read no chars
    if (len == 0) {
      return 0;
    }

    int c = currentReader.read(cbuf, off, len);

    if (c < 0) // EOF
    {
      // try next stream
      nextReader();
      return read(cbuf, off, len);
    }

    return c;
  }

  /**
   * Reads a single character.
   *
   * @return character read or -1 if EOF
   * @throws IOException if an error occurs
   */
  @Override public int read() throws IOException {
    if (currentReader == null) {
      return -1; // at end of all streams
    }

    int c = currentReader.read();

    if (c < 0) {
      // try next stream
      nextReader();
      return read();
    }

    return c;
  }

  /**
   * Reads characters into a buffer.
   *
   * @param cbuf character buffer
   * @return number of characters read or -1 on EOF
   * @throws IOException if an error occurs
   */
  @Override public int read(char[] cbuf) throws IOException {
    return read(cbuf, 0, cbuf.length);
  }

  /**
   * Reads characters into a buffer.
   *
   * @param target character buffer to read into
   * @return number of characters read or -1 on EOF
   * @throws IOException if an error occurs
   */
  @Override public int read(CharBuffer target) throws IOException {
    if (currentReader == null) {
      return -1; // at end of all streams
    }

    int c = currentReader.read(target);

    if (c < 0) {
      // try next stream
      nextReader();
      return read(target);
    }

    return c;
  }

  /**
   * Determines if the underlying stream is ready.
   *
   * @throws IOException if an error occurs return true if ready, false otherwise
   */
  @Override public boolean ready() throws IOException {
    return (currentReader == null) ? false : currentReader.ready();
  }

  /**
   * Skips the specified number of characters.
   *
   * @return number of characters actually skipped
   * @throws IOException if an error occurs
   */
  @Override public long skip(long n) throws IOException {
    if (n < 0) {
      throw new IllegalArgumentException("skip value is negative");
    }

    // do nothing for 0 skip
    if (n == 0) {
      return 0;
    }

    if (currentReader == null) {
      return 0;
    }

    long c = currentReader.skip(n);

    if (c <= 0) {
      // we appear to be at the end of the stream. double-check by
      // attempting
      // a single read on the current stream.
      if (read() < 0) {
        // we are definitely at the end
        nextReader();
        return skip(n);
      }

      // read() succeeded. notify caller that 1 char was skipped.
      return 1;
    }

    return c;
  }

  /**
   * Closes the reader.
   *
   * @throws IOException if any underlying stream throws an exception
   */
  @Override public void close() throws IOException {
    while (currentReader != null) {
      nextReader();
    }
  }

  /**
   * Marks the position in the current stream.
   *
   * <p>
   * This implementation does not support marks.
   * </p>
   *
   * @param readAheadLimit ignored
   * @throws IOException always
   */
  @Override public void mark(int readAheadLimit) throws IOException {
    throw new IOException("mark() not supported");
  }

  /**
   * Determines if mark is supported.
   *
   * <p>
   * This implementation does not support marks.
   * </p>
   *
   * @return false
   */
  @Override public boolean markSupported() {
    return false;
  }

  /**
   * Resets the stream back to the current mark.
   *
   * <p>
   * This implementation does not support reset.
   * </p>
   *
   * @throws IOException always
   */
  @Override public void reset() throws IOException {
    throw new IOException("reset() not supported");
  }

  private void nextReader() throws IOException {
    if (currentReader != null) {
      currentReader.close();
    }

    if (readerIterator.hasNext()) {
      currentReader = readerIterator.next();
      if (currentReader == null) {
        throw new NullPointerException();
      }
    } else {
      currentReader = null;
    }
  }
}
