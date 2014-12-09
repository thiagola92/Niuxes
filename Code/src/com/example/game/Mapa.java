package com.example.game;

public class Mapa {

	public int[][] mapaPos = {	{ 0, 0, 0, 0, 0, 0, 0, },
								{ 0, 0, 0, 0, 0, 0, 0, },
								{ 0, 0, 0, 0, 0, 0, 0, },
								{ 0, 0, 0, 0, 0, 0, 0, },
								{ 0, 0, 0, 0, 0, 0, 0, },
								{ 0, 0, 0, 0, 0, 0, 0, },
								{ 0, 0, 0, 0, 0, 0, 0, }};
	
	public Mapa (int a, int b, int c,
				int x, int y, int z) {
		
		mapaPos[6][1] = a;
		mapaPos[6][3] = b;
		mapaPos[6][5] = c;
		
		mapaPos[0][1] = x;
		mapaPos[0][3] = y;
		mapaPos[0][5] = z;
	}
	
	public void inverter () {
		int i, j;
		
		for (i=0; i<=6; i++) {
			for (j=0; j<=6; j++) {
				mapaPos[i][j]*=-1;
			}
		}
	}
	
	public void receberDeBytes(byte[] objeto) {
		mapaPos[0][0] = objeto[0];
		mapaPos[0][1] = objeto[1];
		mapaPos[0][2] = objeto[2];
		mapaPos[0][3] = objeto[3];
		mapaPos[0][4] = objeto[4];
		mapaPos[0][5] = objeto[5];
		mapaPos[0][6] = objeto[6];

		mapaPos[1][0] = objeto[7];
		mapaPos[1][1] = objeto[8];
		mapaPos[1][2] = objeto[9];
		mapaPos[1][3] = objeto[10];
		mapaPos[1][4] = objeto[11];
		mapaPos[1][5] = objeto[12];
		mapaPos[1][6] = objeto[13];

		mapaPos[2][0] = objeto[14];
		mapaPos[2][1] = objeto[15];
		mapaPos[2][2] = objeto[16];
		mapaPos[2][3] = objeto[17];
		mapaPos[2][4] = objeto[18];
		mapaPos[2][5] = objeto[19];
		mapaPos[2][6] = objeto[20];

		mapaPos[3][0] = objeto[21];
		mapaPos[3][1] = objeto[22];
		mapaPos[3][2] = objeto[23];
		mapaPos[3][3] = objeto[24];
		mapaPos[3][4] = objeto[25];
		mapaPos[3][5] = objeto[26];
		mapaPos[3][6] = objeto[27];

		mapaPos[4][0] = objeto[28];
		mapaPos[4][1] = objeto[29];
		mapaPos[4][2] = objeto[30];
		mapaPos[4][3] = objeto[31];
		mapaPos[4][4] = objeto[32];
		mapaPos[4][5] = objeto[33];
		mapaPos[4][6] = objeto[34];
		
		mapaPos[5][0] = objeto[35];
		mapaPos[5][1] = objeto[36];
		mapaPos[5][2] = objeto[37];
		mapaPos[5][3] = objeto[38];
		mapaPos[5][4] = objeto[39];
		mapaPos[5][5] = objeto[40];
		mapaPos[5][6] = objeto[41];

		mapaPos[6][0] = objeto[42];
		mapaPos[6][1] = objeto[43];
		mapaPos[6][2] = objeto[44];
		mapaPos[6][3] = objeto[45];
		mapaPos[6][4] = objeto[46];
		mapaPos[6][5] = objeto[47];
		mapaPos[6][6] = objeto[48];
	}
	
	public byte[] converterParaBytes() {
		byte[] quadrados = { (byte) mapaPos[0][0],
								(byte) mapaPos[0][1],
								(byte) mapaPos[0][2],
								(byte) mapaPos[0][3],
								(byte) mapaPos[0][4],
								(byte) mapaPos[0][5],
								(byte) mapaPos[0][6],
								
								(byte) mapaPos[1][0],
								(byte) mapaPos[1][1],
								(byte) mapaPos[1][2],
								(byte) mapaPos[1][3],
								(byte) mapaPos[1][4],
								(byte) mapaPos[1][5],
								(byte) mapaPos[1][6],
								
								(byte) mapaPos[2][0],
								(byte) mapaPos[2][1],
								(byte) mapaPos[2][2],
								(byte) mapaPos[2][3],
								(byte) mapaPos[2][4],
								(byte) mapaPos[2][5],
								(byte) mapaPos[2][6],
								
								(byte) mapaPos[3][0],
								(byte) mapaPos[3][1],
								(byte) mapaPos[3][2],
								(byte) mapaPos[3][3],
								(byte) mapaPos[3][4],
								(byte) mapaPos[3][5],
								(byte) mapaPos[3][6],
								
								(byte) mapaPos[4][0],
								(byte) mapaPos[4][1],
								(byte) mapaPos[4][2],
								(byte) mapaPos[4][3],
								(byte) mapaPos[4][4],
								(byte) mapaPos[4][5],
								(byte) mapaPos[4][6],
								
								(byte) mapaPos[5][0],
								(byte) mapaPos[5][1],
								(byte) mapaPos[5][2],
								(byte) mapaPos[5][3],
								(byte) mapaPos[5][4],
								(byte) mapaPos[5][5],
								(byte) mapaPos[5][6],
								
								(byte) mapaPos[6][0],
								(byte) mapaPos[6][1],
								(byte) mapaPos[6][2],
								(byte) mapaPos[6][3],
								(byte) mapaPos[6][4],
								(byte) mapaPos[6][5],
								(byte) mapaPos[6][6],
		};
		
		return quadrados;
	}
}
