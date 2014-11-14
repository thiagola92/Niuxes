package com.example.game;

import java.util.Random;

public class Mapa {
	
	final static int NUMERO_DE_PECAS = 4;

	public int[][] mapaPos = {	{ 0, 0, 0, 0, 0, 0, 0, },
								{ 0, 0, 0, 0, 0, 0, 0, },
								{ 0, 0, 0, 0, 0, 0, 0, },
								{ 0, 0, 0, 0, 0, 0, 0, },
								{ 0, 0, 0, 0, 0, 0, 0, },
								{ 0, 0, 0, 0, 0, 0, 0, },
								{ 0, 0, 0, 0, 0, 0, 0, }};
	
	public Mapa (int a, int b, int c) {
		
		Random pecaRandomica = new Random();
		
		mapaPos[0][1] = (pecaRandomica.nextInt(NUMERO_DE_PECAS)+1)*(-1);
		mapaPos[0][3] = (pecaRandomica.nextInt(NUMERO_DE_PECAS)+1)*(-1);
		mapaPos[0][5] = (pecaRandomica.nextInt(NUMERO_DE_PECAS)+1)*(-1);
		
		mapaPos[6][1] = a;
		mapaPos[6][3] = b;
		mapaPos[6][5] = c;
	}
	
	public Mapa (int a, int b, int c,
				int x, int y, int z) {
		
		mapaPos[6][1] = a;
		mapaPos[6][3] = b;
		mapaPos[6][5] = c;
		
		mapaPos[0][1] = x;
		mapaPos[0][3] = y;
		mapaPos[0][5] = z;
	}
}
