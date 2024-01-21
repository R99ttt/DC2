# Huffman Coding Compression and Decompression

## Overview
This project implements a simple compression and decompression algorithm using Huffman coding. The Huffman algorithm is a widely used technique for lossless data compression.

## Files

### `Compressor` Interface
The `Compressor` interface defines the methods that any compressor implementation should have. It includes methods for compression, decompression, and versions of these operations that return the compressed data as byte arrays.

```java
public interface Compressor {
    void Compress(String[] input_names, String[] output_names) throws FileNotFoundException, IOException;
    void Decompress(String[] input_names, String[] output_names) throws ClassNotFoundException, IOException;
    byte[] CompressWithArray(String[] input_names, String[] output_names);
    byte[] DecompressWithArray(String[] input_names, String[] output_names);
}
```

### `HufEncoderDecoder` Class
The `HufEncoderDecoder` class implements the `Compressor` interface. It performs compression and decompression using Huffman coding.

#### Compression
The `Compress` method takes an array of input file names and an array of output file names. It compresses the input files using Huffman coding and writes the compressed data to the output files.

#### Decompression
The `Decompress` method takes an array of input file names and an array of output file names. It decompresses the input files using Huffman coding and writes the decompressed data to the output files.

#### Helper Methods
- `huffmanTreeBuilder`: Builds a Huffman tree based on the frequency map of byte pairs.
- `nodesEncoder`: Assigns Huffman codes to each leaf node in the Huffman tree.
- `huffCodeToFile`: Writes the Huffman codes for each byte pair to the output file.
- `convertStringBit2`: Converts a binary string to a list of bytes with proper padding.
- `frequencies`: Calculates the frequency of each byte pair in the input file.

### `Node` Class
The `Node` class represents a node in the Huffman tree. Each node has a left child, a right child, a list of bytes, and a frequency.

## Usage
1. Create an instance of the `HufEncoderDecoder` class.
2. Call the `Compress` method with input and output file names to compress data.
3. Call the `Decompress` method with input and output file names to decompress data.

```java
HufEncoderDecoder compressor = new HufEncoderDecoder();
compressor.Compress(new String[]{"input.txt"}, new String[]{"compressed.bin"});
compressor.Decompress(new String[]{"compressed.bin"}, new String[]{"output.txt"});
```

## Contributors
- Rami Abo Rabia
- Itamar Abir


## Notes
- Uncomment the import statements for `FileInputStream` and `FileOutputStream` if you wish to use them for file access.
- The code includes comments explaining key steps and decisions.
- Various methods and alternative approaches are mentioned in comments but are not used in the final implementation.

Feel free to reach out to the contributors for any questions or clarifications.
