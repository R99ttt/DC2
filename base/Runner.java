package base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import assign01.HufEncoderDecoder;

public class Runner {
	public static void main(String[] args) {


//		String test = "C:\\Users\\user\\eclipse-workspace\\compress_huffman\\src\\test.txt";
//		String pathSmile = "C:\\Users\\user\\eclipse-workspace\\compress_huffman\\src\\Smiley.bmp";
//		String pathRomeo = "C:\\Users\\user\\eclipse-workspace\\compress_huffman\\src\\Romeo and Juliet  Entire Play.txt";
//		String pathOnTheOrigin = "C:\\Users\\user\\eclipse-workspace\\compress_huffman\\src\\OnTheOrigin.txt";
		
		String test = "C:\\Users\\asus\\eclipse-workspace\\comp_final\\src\\test.txt";
		String pathSmile = "C:\\Users\\user\\eclipse-workspace\\compress_huffman\\src\\Smiley.bmp";
		String pathRomeo = "C:\\Users\\asus\\eclipse-workspace\\comp_final\\src\\Romeo and Juliet  Entire Play.txt";
		String pathOnTheOrigin = "C:\\Users\\user\\eclipse-workspace\\compress_huffman\\src\\OnTheOrigin.txt";
		
		String flower = "C:\\Users\\asus\\eclipse-workspace\\comp_final\\src\\Red_Flowers.bmp";

		String encoded = "encoded.bin";
		String[] input = new String[5];
		String[] output = new String[5];
		String decoded = "decoded.bin";
		String[] decode = new String[2];
		
		decode[0] = decoded;
		input[0] = flower;
		output[0] = encoded;

		HufEncoderDecoder huff = new HufEncoderDecoder();
		try {
			huff.Compress(input, output);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			huff.Decompress(output, decode);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

	}
}
