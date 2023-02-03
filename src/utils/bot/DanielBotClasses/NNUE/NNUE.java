package utils.bot.DanielBotClasses.NNUE;

import static utils.bot.DanielBotClasses.NNUE.NNUESettings.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;

//import jdk.incubator.vector.FloatVector;

public class NNUE {
	static NN_Storage storage = new NN_Storage();
	
	/**
	 * I/O FUNCTIONS
	 */
	
	static boolean nn_convert(String filename) {
		BufferedReader reader;
		
		try {
			reader = new BufferedReader(new FileReader("network.txt"));
			
			float value;
			String line;
			
			for (int col = 0; col < NN_SIZE; col++) {
				for (int row = 0; row < 40960; row++) {
					line = reader.readLine();
					if (line == null) {
						return false;
					}
					
					value = Float.parseFloat(line);
					
					if (value < -THRESHOLD || value > THRESHOLD) {
						return false;
					}
					
					storage.W0[row * NN_SIZE + col] = (short) (value * FACTOR);
				}
			}
			
			for (int row = 0; row < NN_SIZE; row++) {
				line = reader.readLine();
				if (line == null) {
					return false;
				}
				
				value = Float.parseFloat(line);
				
				storage.B0[row] = value;
			}
			
			for (int col = 0; col < 32; col++) {
				for (int row = 0; row < (NN_SIZE * 2); row++) {
					line = reader.readLine();
					if (line == null) {
						return false;
					}
					
					value = Float.parseFloat(line);
					
					storage.W1[row * 32 + col] = value;
				}
			}
			
			for (int row = 0; row < 32; row++) {
				line = reader.readLine();
				if (line == null) {
					return false;
				}
				
				value = Float.parseFloat(line);
				
				storage.B1[row] = value;
			}
			
			for (int col = 0; col < 32; col++) {
				for (int row = 0; row < 32; row++) {
					line = reader.readLine();
					if (line == null) {
						return false;
					}
					
					value = Float.parseFloat(line);
					
					storage.W2[row * 32 + col] = value;
				}
			}
			
			for (int row = 0; row < 32; row++) {
				line = reader.readLine();
				if (line == null) {
					return false;
				}
				
				value = Float.parseFloat(line);
				
				storage.B2[row] = value;
			}
			
			for (int col = 0; col < 1; col++) {
				for (int row = 0; row < 32; row++) {
					line = reader.readLine();
					if (line == null) {
						return false;
					}
					
					value = Float.parseFloat(line);
					
					storage.W3[row + col] = value;
				}
			}
			
			for (int row = 0; row < 1; row++) {
				line = reader.readLine();
				if (line == null) {
					return false;
				}
				
				value = Float.parseFloat(line);
				
				storage.B3[row] = value;
			}
			
			float W1[] = storage.W1.clone();
			float W2[] = storage.W2.clone();
			
			for (int col = 0; col < 32; col++) {
				for (int row = 0; row < (NN_SIZE * 2); row++) {
					storage.W1[col * (NN_SIZE * 2) + row] = W1[row * 32 + col];
				}
			}
			
			for (int col = 0; col < 32; col++) {
				for (int row = 0; row < 32; row++) {
					storage.W2[col * 32 + row] = W2[row * 32 + col];
				}
			}
			
			File outputFile = new File(filename);
			
			if (outputFile.createNewFile()) {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFile));
				
				out.writeObject(storage);
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean nn_load(NN_Network nn, String filename) {
		storage = new NN_Storage();
		
		File file = new File(filename);
		
		if (!file.exists()) {
			if (!nn_convert(filename)) {
				return false;
			}
		}
		
		file = new File(filename);
		
		if (!file.exists()) {
			return false;
		}
		
		try {
			ByteBuffer byteBuffer = ByteBuffer.wrap(Files.readAllBytes(file.toPath()));
			
			byteBuffer = byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			
			byteBuffer.asShortBuffer().get(storage.W0);
			byteBuffer.position(storage.W0.length * 2);
			byteBuffer.asFloatBuffer().get(storage.B0);
			byteBuffer.position(storage.W0.length * 2 + storage.B0.length * 4);
			byteBuffer.asFloatBuffer().get(storage.W1);
			byteBuffer.position(storage.W0.length * 2 + storage.B0.length * 4 + storage.W1.length * 4);
			byteBuffer.asFloatBuffer().get(storage.B1);
			byteBuffer.position(storage.W0.length * 2 + storage.B0.length * 4 + storage.W1.length * 4 + storage.B1.length * 4);
			byteBuffer.asFloatBuffer().get(storage.W2);
			byteBuffer.position(storage.W0.length * 2 + storage.B0.length * 4 + storage.W1.length * 4 + storage.B1.length * 4 + storage.W2.length * 4);
			byteBuffer.asFloatBuffer().get(storage.B2);
			byteBuffer.position(storage.W0.length * 2 + storage.B0.length * 4 + storage.W1.length * 4 + storage.B1.length * 4 + storage.W2.length * 4 + storage.B2.length * 4);
			byteBuffer.asFloatBuffer().get(storage.W3);
			byteBuffer.position(storage.W0.length * 2 + storage.B0.length * 4 + storage.W1.length * 4 + storage.B1.length * 4 + storage.W2.length * 4 + storage.B2.length * 4 + storage.W3.length * 4);
			byteBuffer.asFloatBuffer().get(storage.B3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < storage.W0.length; i++) {
			nn.W0[i] = (float) storage.W0[i] / FACTOR;
		}
		
		nn.W1 = storage.W1;
		nn.W2 = storage.W2;
		nn.W3 = storage.W3;
		
		nn.B0 = storage.B0;
		nn.B1 = storage.B1;
		nn.B2 = storage.B2;
		nn.B3 = storage.B3;
		
		return true;
	}
	
	/**
	 * HELPER FUNCTIONS
	 */
	
	static int NN_GET_POSITION(long pieces) { return Long.numberOfTrailingZeros(pieces); }
	
	static float clamp(float value) {
		if (value < NN_RELU_MIN) {
			return NN_RELU_MIN;
		} else if (value > NN_RELU_MAX) {
			return NN_RELU_MAX;
		}
		
		return value;
	}
	
	/**
	 * EVALUATION
	 */
	
	static void nn_compute_layer(float B[], float I[], float W[], float O[], int idim, int odim, boolean with_relu) {
		for (int o = 0; o < odim; o++) {
			float sum = B[o];
			
			for (int i = 0; i < idim; i++) {
				sum += W[o * idim + i] * I[i];
			}
			
			if (with_relu) {
				O[o] = clamp(sum);
			} else {
				O[o] = sum;
			}
		}
	}
	
	public static int nn_eval(NN_Network nn, NN_Board board, int color) {
		float O0[] = new float[NN_SIZE * 2];
		
		for (int o = 0; o < NN_SIZE; o++) {
			O0[o] = clamp(board.accumulator[color][o]);
			O0[o + NN_SIZE] = clamp(board.accumulator[1 - color][o]);
		}
		
		float O1[] = new float[32];
		float O2[] = new float[32];
		float O3[] = new float[1];
		
		nn_compute_layer(nn.B1, O0, nn.W1, O1, NN_SIZE * 2, 32, true);
		nn_compute_layer(nn.B2, O1, nn.W2, O2, 32, 32, true);
		nn_compute_layer(nn.B3, O2, nn.W3, O3, 32, 1, false);
		return (int) (O3[0] * 100);
	}
	
	/**
	 * Other stuff
	 */
	
	public static void nn_inputs_upd_all(NN_Network nn, NN_Board board) {
		board.accumulator[0] = nn.B0.clone();
		board.accumulator[1] = nn.B0.clone();
	
		
		for (int piece_color = 0; piece_color <= 1; piece_color++) {
			for (int piece_type = 0; piece_type <= 4; piece_type++) {
				long pieces = board.pieces[piece_type + 6 * piece_color];
				
				while (pieces != 0) {
					int piece_position = NN_GET_POSITION(pieces);
					nn_inputs_add_piece(nn, board, piece_type, piece_color, piece_position);
					pieces &= pieces - 1;
				}
			}
		}
	}
	
	public static void nn_inputs_add_piece(NN_Network nn, NN_Board board, int piece_type, int piece_color, int piece_position) {
		int white_king_position = NN_GET_POSITION(board.pieces[5]);
		int black_king_position = NN_GET_POSITION(board.pieces[11]) ^ 63;
		
		int index_w = (piece_type << 1) + piece_color;
		int index_b = (piece_type << 1) + (1 - piece_color);
		
		int sq_w = piece_position;
		int sq_b = piece_position ^ 63;
		
		int feature_w = (640 * white_king_position) + (64 * index_w) + (sq_w);
		int feature_b = (640 * black_king_position) + (64 * index_b) + (sq_b);
		
		for (int o = 0; o < NN_SIZE; o++) {
			board.accumulator[0][o] += nn.W0[NN_SIZE * feature_w + o];
			board.accumulator[1][o] += nn.W0[NN_SIZE * feature_b + o];
		}
	}
	
	public static void nn_inputs_del_piece(NN_Network nn, NN_Board board, int piece_type, int piece_color, int piece_position) {
		int white_king_position = NN_GET_POSITION(board.pieces[5]);
		int black_king_position = NN_GET_POSITION(board.pieces[11]) ^ 63;
		
		int index_w = (piece_type << 1) + piece_color;
		int index_b = (piece_type << 1) + (1 - piece_color);
		
		int sq_w = piece_position;
		int sq_b = piece_position ^ 63;
		
		int feature_w = (640 * white_king_position) + (64 * index_w) + (sq_w);
		int feature_b = (640 * black_king_position) + (64 * index_b) + (sq_b);
		
		for (int o = 0; o < NN_SIZE; o++) {
			board.accumulator[0][o] -= nn.W0[NN_SIZE * feature_w + o];
			board.accumulator[1][o] -= nn.W0[NN_SIZE * feature_b + o];
		}
	}
	
	public static void nn_inputs_mov_piece(NN_Network nn, NN_Board board, int piece_type, int piece_color, int from, int to) {
		nn_inputs_add_piece(nn, board, piece_type, piece_color, to);
		nn_inputs_del_piece(nn, board, piece_type, piece_color, from);
	}
}