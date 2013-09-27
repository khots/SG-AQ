package edu.wustl.query.flex.dag;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import junit.framework.TestCase;

public class DAGPathTestCase extends TestCase
{
	public void testAllMethods() throws IOException, ClassNotFoundException
	{
		DAGPath dagPath = new DAGPath();
		dagPath.setDestinationExpId(2);
		dagPath.setId("2");
		dagPath.setSelected(false);
		dagPath.setSourceExpId(1);
		dagPath.setToolTip("Participant");

		dagPath.getDestinationExpId();
		dagPath.getId();
		dagPath.getSourceExpId();
		dagPath.getToolTip();
		dagPath.isSelected();

		ObjectOutput op = new ObjectOutput()
		{

			public void writeUTF(String arg0) throws IOException {
				// TODO Auto-generated method stub

			}

			public void writeShort(int arg0) throws IOException {
				// TODO Auto-generated method stub

			}

			public void writeLong(long arg0) throws IOException {
				// TODO Auto-generated method stub

			}

			public void writeInt(int arg0) throws IOException {
				// TODO Auto-generated method stub

			}

			public void writeFloat(float arg0) throws IOException {
				// TODO Auto-generated method stub

			}

			public void writeDouble(double arg0) throws IOException {
				// TODO Auto-generated method stub

			}

			public void writeChars(String arg0) throws IOException {
				// TODO Auto-generated method stub

			}

			public void writeChar(int arg0) throws IOException {
				// TODO Auto-generated method stub

			}

			public void writeBytes(String arg0) throws IOException {
				// TODO Auto-generated method stub

			}

			public void writeByte(int arg0) throws IOException {
				// TODO Auto-generated method stub

			}

			public void writeBoolean(boolean arg0) throws IOException {
				// TODO Auto-generated method stub

			}

			public void writeObject(Object obj) throws IOException {
				// TODO Auto-generated method stub

			}

			public void write(byte[] b, int off, int len) throws IOException {
				// TODO Auto-generated method stub

			}

			public void write(byte[] b) throws IOException {
				// TODO Auto-generated method stub

			}

			public void write(int b) throws IOException {
				// TODO Auto-generated method stub

			}

			public void flush() throws IOException {
				// TODO Auto-generated method stub

			}

			public void close() throws IOException {
				// TODO Auto-generated method stub

			}
		};

		ObjectInput input = new ObjectInput() {

			public int skipBytes(int arg0) throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public int readUnsignedShort() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public int readUnsignedByte() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public String readUTF() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			public short readShort() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public long readLong() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public String readLine() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			public int readInt() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public void readFully(byte[] arg0, int arg1, int arg2) throws IOException {
				// TODO Auto-generated method stub

			}

			public void readFully(byte[] arg0) throws IOException {
				// TODO Auto-generated method stub

			}

			public float readFloat() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public double readDouble() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public char readChar() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public byte readByte() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public boolean readBoolean() throws IOException {
				// TODO Auto-generated method stub
				return false;
			}

			public long skip(long n) throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public Object readObject() throws ClassNotFoundException, IOException {
				// TODO Auto-generated method stub
				return null;
			}

			public int read(byte[] b, int off, int len) throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public int read(byte[] b) throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public int read() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}

			public void close() throws IOException {
				// TODO Auto-generated method stub

			}

			public int available() throws IOException {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		dagPath.readExternal(input);
		dagPath.writeExternal(op);
	}
}
