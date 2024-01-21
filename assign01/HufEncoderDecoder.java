package assign01;

/**
 * Assignment 1
 * Submitted by: 
 * Student 1. Itamar Abir 	ID# 
 * Student 2. Rami Abo Rabia	ID# 
 */

// Uncomment if you wish to use FileOutputStream and FileInputStream for file access.
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import base.Compressor;

public class HufEncoderDecoder implements Compressor {
	public HufEncoderDecoder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Compress(String[] input_names, String[] output_names) throws IOException {
		//System.out.println("strat compress");
		FileInputStream fis = new FileInputStream(input_names[0]);
		FileOutputStream fos = new FileOutputStream(output_names[0]);
		//saves for each 2 bytes the frequency
		HashMap<List<Byte>, Integer> freqMap = new HashMap<List<Byte>, Integer>();
		frequencies(fis, fos, freqMap);
		//printMap(freqMap);
		
		//a root for a tree will be returned
		Node root = huffmanTreeBuilder(freqMap);
		
		//for each 2 bytes we are saving it code from the huff-algo
		HashMap<List<Byte>, String> encodedBytes = new HashMap<List<Byte>, String>();
		String HuffCode = "";
		
		//this function that gives recursively for each leaf it code
		nodesEncoder(encodedBytes, root, HuffCode);
		//System.out.println("new*****************************");

		//printMap(encodedBytes);
		
		//because there is no smaller unit to save to file than a byte we are building an array of bytes
		//that will save the codes as they are, if we will save the codes as they are there will be duplicates
		//because the transfer can recognize the difference between 000011 and 00011 the both have the same value 3
		HashMap<List<Byte>, List<Byte>> bytesMap = new HashMap<List<Byte>, List<Byte>>();
		
		//System.out.println("@@@@@@"+encodedBytes.size());
		
		//to solve the problem above we used this method. and also we take in account that because there is no
		//smaller unit than a byte a code like 011 still will take 8 bits to write it in the file so for
		//easier approach for decompress we iterate and save for 2 bytes it codes with the bytes that it should take
		compressMap(encodedBytes, bytesMap);
		System.out.println("@@@@@@"+bytesMap.size());
		
		//Map writing: to decompress
		MapToFile(encodedBytes,fos);
		//printMap(bytesMap);
		
		//huff-code writing for each 2 bytes
		huffCodeToFile(bytesMap, fis, fos);

		System.out.println("end compress");


//		String encode = "encoded.bin";
//		String decode = "decoded.bin";
//		test(encode, decode, bytesMap);
		
		//output_names[0]="encoded.txt";
		
		fis.close();
		fos.close();
	}
	// we tried a lot of ways (they are stil in the comments) to reduce the map size but didnt succeed in that.
	//without the map we are getting a compression of like 50% - you can test that on the method called test.
	private void MapToFile(HashMap<List<Byte>, String> encodedBytes, FileOutputStream fos) throws IOException {
		//DataOutputStream dataOut = new DataOutputStream(fos);
		//System.out.println(encodedBytes.size());
		//ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		ObjectOutputStream objectOut = new ObjectOutputStream(fos);
		objectOut.writeObject(encodedBytes);
		//objectOut.close();
		//byte[] byteMap = byteArrayOut.toByteArray();
		// dataOut.writeLong(byteMap.length);
		// System.out.println(byteMap.length);
		//dataOut.write(byteMap);
	}
	

	private void compressMap(HashMap<List<Byte>, String> encodedBytes, HashMap<List<Byte>, List<Byte>> bytesMap) {

		//System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		//printMap(encodedBytes);
		
		//converting each code and saving its uniqueness by writing it from the MSB
		for (List<Byte> name : encodedBytes.keySet()) {
			// System.out.println(encodedBytes.get(name));
			List<Byte> list = convertStringBit2(encodedBytes.get(name));

			//list=convertStringBit2("0011100001");
			bytesMap.put(name, list);
		}

	}
	//this method can show the compression without saving the map, it gives like 50 percent of compression
	//the same code is int the decompress method and will be explained there
	private void test(String input, String output, HashMap<List<Byte>, List<Byte>> bytesMap) throws IOException {
		//System.out.println("start decompress!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		FileInputStream fis = new FileInputStream(input);
		FileOutputStream fos = new FileOutputStream(output);
		
		//ObjectInputStream objectIn = new ObjectInputStream(fis);
		//bytesMap = (HashMap<List<Byte>, List<Byte>>)objectIn.readObject();
		
				
		HashMap<List<Byte>, List<Byte>> flippedMap = new HashMap<>();
		// printMap(bytesMap);

		flippedMap = flipMap(bytesMap);
		//writeMapToFile(flippedMap);

		//printFlip(flippedMap);

//		for (int j = 7; j >= 0; j--) {
//			System.out.print(((m >>> j) & 1));
//		}
		byte[] data = new byte[fis.available()];
		// System.out.println(fis.available());
		fis.read(data);
		List<Byte> iterator = new ArrayList<>();
		
		for (int i = 0; i < data.length; i++) {

			// System.out.println("A");
			iterator.add(data[i]);
			if (flippedMap.containsKey(iterator)) {
				fos.write(flippedMap.get(iterator).get(0));
				//System.out.print((char) Integer.parseInt((flippedMap.get(iterator).get(0)) + ""));

				fos.write(flippedMap.get(iterator).get(1));
				//System.out.print((char) Integer.parseInt((flippedMap.get(iterator).get(1)) + ""));

				iterator.clear();

			}
		}

		// System.out.print(String.format("%8s",
		// Integer.toBinaryString(data[i])).replace(' ', '0'));
//			for (int j = 7; j >= 0; j--) {
//				System.out.print(((data[i] >>> j) & 1));
//			}

		// printMap(flippedMap);

//		for (int i = 0; i < data.length; i++) {
//			System.out.println(data[i]);
//		}
		// System.out.println(data.length);
//		for (byte iterator : data) {
//			bits = "";
//			for (int i = 7; i >= 0; i--) {
//				bits += ((iterator >>> i) & 1);
//				// System.out.println(bits);
//			}
		// System.out.println(bits);
		// char last=bits.charAt(bits.length()-1);
		// bits.substring(0, bits.length() - 1);

		// System.out.println();

		System.out.println("end decompress!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		fis.close();
		fos.close();

	}

//	private void writeMapToFile(HashMap<List<Byte>, List<Byte>> flippedMap) throws IOException {
//
//	}

	// we are using this method for reading the compressed file and making back the docoded file.
	private HashMap<List<Byte>, List<Byte>> flipMap(HashMap<List<Byte>, List<Byte>> bytesMap) {
		HashMap<List<Byte>, List<Byte>> flipMap = new HashMap<>();
		for (Map.Entry<List<Byte>, List<Byte>> entry : bytesMap.entrySet()) {
			flipMap.put(entry.getValue(), entry.getKey());
		}
		return flipMap;

	}
// here we are compressing the data on the file. because we are reading one pair at a time we need to deal with odd
	//number of bytes. to deal with that the last byte always gets frequency of 1 and will have a tail of byte 0
	//and are the rarest/
	public void huffCodeToFile(HashMap<List<Byte>, List<Byte>> bytesMap, FileInputStream fis, FileOutputStream fos)
			throws IOException {

		fis.getChannel().position(0);
		
		int originalSizeOfFile = fis.available();
		
		int fileSize= fis.available();
		 if(fileSize % 2 != 0)
			 fileSize--;
		 
		byte[] data = new byte[fis.available()];// reading all the data at once
		// System.out.println(fis.available());
		fis.read(data);
		
		// int fileSize= fis.available();
		// List<Byte> listByte= new ArrayList<Byte>(2);
		//byte[] arrayByte = new byte[2];
		//int readBytes = 0;
		
		 for(int i = 0; i < fileSize; i+=2) {
			// fis.read(arrayByte, 0, readBytes);

			List<Byte> listByte = new ArrayList<>(2);
			listByte.add(data[i]);
			listByte.add(data[i+1]);

			if (bytesMap.containsKey(listByte)) {

				List<Byte> bitsValue = bytesMap.get(listByte);
				for (int j = 0; j < bitsValue.size(); j++) {
					fos.write(bitsValue.get(j));
				}
			}

		}
		//new:
		 if(originalSizeOfFile % 2 != 0) {
			
			List<Byte> listByte = new ArrayList<>(2);
			listByte.add(data[data.length-1]);//the last element is in index 1
			listByte.add((byte) 0);//adding 0 byte to the "tail" 
			
			//each code can take many bytes so we will iterate over it until we finish the code for each 2 bytes
			List<Byte> bitsValue = bytesMap.get(listByte);
			for (int i = 0; i < bitsValue.size(); i++) {//we dont know how many bytes the code consumes
				fos.write(bitsValue.get(i));
			}
			
		}

	}
	
	//this method takes the code and puts it in byte and holds for each code its uniqueness.
	//it will be written from the left and have only the bytes that he can have from the system. 
	private static List<Byte> convertStringBit2(String bitString) {
		//00111.len = 5 --> 00111000
		String toByteString = bitString;
		int size = (bitString.length() + 7) / 8; //How many bytes needed
		List<Byte> writeByte = new ArrayList<Byte>(size);
		
		for(int i = bitString.length(); i < size*8; i++) {
			toByteString = toByteString +"0"; //to get an full bytes from the MSB so we will have the same code but in byte
		}
		
		for(int i = 0; i < size; i++) {
			String toByte = toByteString.substring(0,8);
			Byte part = (byte) Integer.parseInt(toByte, 2);
			toByteString = toByteString.substring(8);//removing the first 8 bits
			
			writeByte.add(part);
			
		}
		
		return writeByte;
	}
	
	//tried in this way but didnt used that at the end
	private static List<Byte> convertStringBit1(String bitString) {
//		System.out.println();
//		System.out.println("bitString");
//		System.out.println(bitString);
		// BitSet bitSet = new BitSet(bitString.length());
		int size = (bitString.length() + 7) / 8;
		int j = 0;

		// System.out.println("size");
		List<Byte> writeByte = new ArrayList<Byte>(size);
		// System.out.println(size);
		for (int k = 0; k < size; k++) {
			Byte emptyByte = 0;
			writeByte.add(emptyByte);
			// System.out.println(writeByte.size());

			// writeByte.add((byte) 0);
		}
		for (int i = 0; i < bitString.length(); i++) {
			int bit = Integer.parseInt(bitString.charAt(bitString.length() - 1 - i) + "");
			byte bits = writeByte.get(j);
			if (bit == 1) {
				byte b = (byte) 0b10000000;
				bits = (byte) (bits >> 1);
				writeByte.set(j, (byte) (bits | b));
			}
			if (bit == 0) {
				writeByte.set(j, (byte) (bits >>> 1));

			}

			if (i == 7) {
				j++;
			}
			// System.out.println(Integer.parseInt(bitString, 2));

		}

		return writeByte;
	}
	
	//tried in this way also but didnt used that at the end
	private static List<Byte> convertStringBit(String bitString) {
//		System.out.println();
//		System.out.println("bitString");
//		System.out.println(bitString);
		// BitSet bitSet = new BitSet(bitString.length());
		int size = (bitString.length() + 7) / 8;
		int j = 0;

		// System.out.println("size");
		List<Byte> writeByte = new ArrayList<Byte>(size);
		// System.out.println(size);
		for (int k = 0; k < size; k++) {
			Byte emptyByte = 0;
			writeByte.add(emptyByte);
			// System.out.println(writeByte.size());

			// writeByte.add((byte) 0);
		}
		for (int i = 0; i < bitString.length() - 1; i++) {
			int bit = Integer.parseInt(bitString.charAt(i) + "");
			if (bit == 1) {
				byte bits = writeByte.get(j);
				bits += 1;
				writeByte.set(j, bits);

			}
			writeByte.set(j, (byte) (writeByte.get(j) << 1));

			if (i == 7) {
				j++;
			}
			// System.out.println(Integer.parseInt(bitString, 2));

		}
		int bit = Integer.parseInt(bitString.charAt(bitString.length() - 1) + "");
		if (bit == 1) {
			byte bits = writeByte.get(j);
			bits += 1;
			writeByte.set(j, bits);

		}
		return writeByte;
	}

//		int bit = Integer.parseInt(bitString.charAt(bitString.length() - 1) + "");
//		if (bit == 1) {
//			writeByte += 1;
//		}
//
//		for (int j = 7; j >= 0; j--) {
//			System.out.print(((writeByte >>> j) & 1));
//
//		}

//		for (int i = 0; i < bitString.length(); i++) {
//			if (bitString.charAt(i) == '1') {
//				// bitSet.set(i);
//			}
//		}
//		BitSet bitSet = new BitSet();
//		// System.out.println("bitset");
//		// System.out.println();
//		byte[] data = bitSet.toByteArray();
//		for (int i = 0; i < data.length; i++) {
//			for (int j = 7; j >= 0; j--) {
//				// System.out.print(((data[i] >>> j) & 1));
//			}
//			// System.out.println();
//
//		}
	//each leaf gets it code by moving from the root until the end
	public void nodesEncoder(HashMap<List<Byte>, String> encodedBytes, Node encodeIt, String itsCode) {
		List<Byte> emptyList = new ArrayList<>(2);
		emptyList.add(null);
		emptyList.add(null);

		if (!encodeIt.isLeaf()) {
			nodesEncoder(encodedBytes, encodeIt.getLeft(), itsCode + "0");
			nodesEncoder(encodedBytes, encodeIt.getRight(), itsCode + "1");
		} else {
			encodedBytes.put(encodeIt.getListByte(), itsCode);
			// System.out.println(itsCode);
		}

	}
	//building the PQ in a circular way that at the end the parent node has the overall sum of the frequency
	public Node huffmanTreeBuilder(HashMap<List<Byte>, Integer> freqMap) {

		PriorityQueue<Node> pQueue = new PriorityQueue<Node>();

		for (List<Byte> key : freqMap.keySet()) { 
			Node node = new Node(null, null, key, freqMap.get(key));
			pQueue.add(node);
		}
		List<Byte> emptyList = new ArrayList<>(2);
		//we dont care what each parent has in its byte. only the leafs are useful
		Byte emptyByte = 0;
		emptyList.add(emptyByte);

		emptyList.add(emptyByte);
		
		//only one pair handling:
		if (pQueue.size() == 1) {
			pQueue.add(new Node(null, null, emptyList, 1));
		}
		
		//Connecting the nodes using the PQ and they are sorted thanks to its features 
		while (pQueue.size() > 1) {
			Node left = pQueue.poll();

			Node right = pQueue.poll();

			Node parent = new Node(left, right, emptyList, left.getFreq() + right.getFreq());
			pQueue.add(parent);
		}

		return pQueue.poll();
	}
	//For testing:
	public void printFlip(HashMap<List<Byte>, List<Byte>> map) {
		System.out.println("print flip map:");
		for (List<Byte> name : map.keySet()) {

			List<Byte> value = map.get(name);

			System.out.println();
			System.out.print((char) Integer.parseInt(value.get(0) + ""));

			System.out.print((char) Integer.parseInt(value.get(1) + ""));
			System.out.println();

		}
		System.out.println("finish print");

	}
	//for testing:
	public <T, U> void printMap(HashMap<U, T> freqMap) {
		System.out.println("printMap:");
		for (U name : freqMap.keySet()) {
			String key = name.toString();

			String value = freqMap.get(name).toString();

			System.out.println(key + " " + value);
		}
	}
	//like huffCodeToFile method.
	//here we are using the HashMap to find each pair end increases its frequency by one each time we find it in the file
	public void frequencies(FileInputStream fis, FileOutputStream fos, HashMap<List<Byte>, Integer> freqMap)
			throws IOException {
		
		int originalSizeOfFile = fis.available();
		
		int fileSize= fis.available();
		 if(fileSize % 2 != 0)
			 fileSize--;
		 
		// List<Byte> listByte= new ArrayList<Byte>(2);
		
		//byte[] arrayByte = new byte[2];
		//int readBytes;
		
		byte[] data = new byte[fis.available()];
		// System.out.println(fis.available());
		fis.read(data);
		
		for(int i = 0; i < fileSize; i+=2) {//when its odd length readBytes == 1
			// fis.read(arrayByte, 0, 2);
			//String c = new String(arrayByte);
			// System.out.println(c);
			List<Byte> listByte = new ArrayList<>(2);
			
			//new:
			listByte.add(data[i]);
			listByte.add(data[i+1]);
					
				//listByte.add(arrayByte[1]);
				
			if (freqMap.containsKey(listByte)) {
				freqMap.put(listByte, freqMap.get(listByte) + 1);
			} else {
				freqMap.put(listByte, 1);

			}
		}
		//odd files length handling:
		if(originalSizeOfFile % 2 != 0) {
			
			List<Byte> listByte = new ArrayList<>(2);
			listByte.add(data[data.length-1]);//the last element is in index 1
			listByte.add((byte) 0);//adding 0 byte to the "tail" 
			freqMap.put(listByte, 1);
			
		}
	}
		



	@Override
	public void Decompress(String[] input_names, String[] output_names) throws ClassNotFoundException, IOException {
		
		System.out.println("start decompress!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		//output_names[0]="decoded.txt";
		// Path p = Paths.get(input_names[0]);
		
		//reading the Map:
		FileInputStream fis = new FileInputStream(input_names[0]);
		
		FileOutputStream fos = new FileOutputStream(output_names[0]);
		//DataInputStream dataInput = new DataInputStream(fis);
		//long sizeOfMap = dataInput.readLong();

		//byte[] byteMap = new byte[(int) sizeOfMap];
		//dataInput.readFully(byteMap);

		//ByteArrayInputStream ByteArrayInput = new ByteArrayInputStream(byteMap);
		ObjectInputStream objectInput = new ObjectInputStream(fis);
		
		HashMap<List<Byte>, List<Byte>> bytesMap = new HashMap<List<Byte>, List<Byte>>();
		HashMap<List<Byte>, String> encodedBytes = new HashMap<List<Byte>, String>();
		
		encodedBytes = (HashMap<List<Byte>, String>) objectInput.readObject();
		
		//getting back the encodedBytes map
		compressMap(encodedBytes, bytesMap);
		//System.out.println("@@@@@@"+bytesMap.size());
		
		//System.out.println("&&&&&"+bytesMap.size());
				
		HashMap<List<Byte>, List<Byte>> flippedMap = new HashMap<>();
		//printMap(bytesMap);
		
		//to be able to read the file:
		flippedMap = flipMap(bytesMap);
		//writeMapToFile(flippedMap);

		//printFlip(flippedMap);

//		for (int j = 7; j >= 0; j--) {
//			System.out.print(((m >>> j) & 1));
//		}
		byte[] data = new byte[fis.available()];
		// System.out.println(fis.available());
		fis.read(data);
		List<Byte> iterator = new ArrayList<>();
		
		for (int i = 0; i < data.length; i++) {

			// System.out.println("A");
			iterator.add(data[i]);
			
			//because the code is unique we need to find the first list of bytes that in the map
			if (flippedMap.containsKey(iterator)) {
				
				//writing back each byte
				fos.write(flippedMap.get(iterator).get(0));
				//System.out.print((char) Integer.parseInt((flippedMap.get(iterator).get(0)) + ""));

				fos.write(flippedMap.get(iterator).get(1));
				//System.out.print((char) Integer.parseInt((flippedMap.get(iterator).get(1)) + ""));

				iterator.clear();

			}
		}

		// System.out.print(String.format("%8s",
		// Integer.toBinaryString(data[i])).replace(' ', '0'));
//			for (int j = 7; j >= 0; j--) {
//				System.out.print(((data[i] >>> j) & 1));
//			}

		// printMap(flippedMap);

//		for (int i = 0; i < data.length; i++) {
//			System.out.println(data[i]);
//		}
		// System.out.println(data.length);
//		for (byte iterator : data) {
//			bits = "";
//			for (int i = 7; i >= 0; i--) {
//				bits += ((iterator >>> i) & 1);
//				// System.out.println(bits);
//			}
		// System.out.println(bits);
		// char last=bits.charAt(bits.length()-1);
		// bits.substring(0, bits.length() - 1);

		// System.out.println();

		System.out.println("end decompress!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		fis.close();
		fos.close();
		
		
	}

	@Override
	public byte[] CompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] DecompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
		return null;
	}

}
