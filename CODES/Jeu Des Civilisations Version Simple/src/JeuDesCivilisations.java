/*******************************************************************************
 * Copyright (C) 2015-2016 ROMAINPC, Nono13064, theroxas898
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class JeuDesCivilisations extends JFrame {
	//VERSION SIMPLE
	//PAR ROMAINPC Nono13064 theroxas898
	
	//Variables:
	public int nombreCivilisations = 10;
	public long temps = 0L;
	//tableau de génération de la carte:
	//0=climat    1=ressources AL    2=ressources EEM   3=appartenance de la case
	public int[][][] monde = new int[30][50][4];
	//tableau des civilisations:
	//0->R	1->G	2->B	3->population	4->production	5->économie	6->influence
	//7->bonheur	8->culture	9->armée	10->éducation	11->science
	//12->nourriture	13->politique	14->politique territoriale	15->AL total	16->EEM total
	//17->cases possédées	18->1=décès2=alliance	19-> pop provisoire	
	//20->est en guerre avec qq`(1=oui)		21->nb batailles à faire	NB:int[0][x]->case n'appartenant à personne
	public int[][] civilisations = new int[nombreCivilisations + 1][22];
	//tableau des relations:
	public int[][] relations = new int[nombreCivilisations][nombreCivilisations - 1];
	//tableau des relations provisoires:
	public double[][] relationsProv = new double[nombreCivilisations][nombreCivilisations - 1];
	
	
	
	
	//création de la Fenetre:
	public JeuDesCivilisations(){
		//paramétrage:
		this.setTitle("Jeu des Civilisations");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setResizable(false);
		this.setSize(1350, 720);
		this.setLocationRelativeTo(null);
		this.setBackground(new Color(100, 100, 100));
		
		//visibilité:
		this.setVisible(true);
		
		//définition du content pane:
		Panneau container = new Panneau();
		this.setContentPane(container);
		
		//debut du programme:
		Go();
	}
	
	
	
	//fonction principale:
	public void Go(){
		
		//génération de la carte:
		for(int ligne = 0 ; ligne <= 29 ; ligne++){
			for(int colonne = 0 ; colonne <= 49 ; colonne++){
				monde[ligne][colonne][0] = (int)(Math.random() * 7);
				monde[ligne][colonne][1] = (int)(Math.random() * 10);
				monde[ligne][colonne][2] = (int)(Math.random() * 10);
			}
		}
		
		
		//génération des Civilisations:
		for(int loop = 1 ; loop <= nombreCivilisations ; loop++){
			civilisations[0][0] = 75; civilisations[0][1] = 75; civilisations[0][2] = 75;
			//couleurs:
			switch(loop){
			case 1:
				civilisations[loop][0] = 0;
				civilisations[loop][1] = 0;
				civilisations[loop][2] = 255;
			break;
			case 2:
				civilisations[loop][0] = 255;
				civilisations[loop][1] = 0;
				civilisations[loop][2] = 0;
			break;
			case 3:
				civilisations[loop][0] = 0;
				civilisations[loop][1] = 255;
				civilisations[loop][2] = 0;
			break;
			case 4:
				civilisations[loop][0] = 255;
				civilisations[loop][1] = 0;
				civilisations[loop][2] = 255;
			break;
			case 5:
				civilisations[loop][0] = 255;
				civilisations[loop][1] = 255;
				civilisations[loop][2] = 0;
			break;
			case 6:
				civilisations[loop][0] = 0;
				civilisations[loop][1] = 255;
				civilisations[loop][2] = 255;
			break;
			case 7:
				civilisations[loop][0] = 100;
				civilisations[loop][1] = 0;
				civilisations[loop][2] = 0;
			break;
			case 8:
				civilisations[loop][0] = 0;
				civilisations[loop][1] = 0;
				civilisations[loop][2] = 100;
			break;
			case 9:
				civilisations[loop][0] = 0;
				civilisations[loop][1] = 100;
				civilisations[loop][2] = 0;
			break;
			case 10:
				civilisations[loop][0] = 255;
				civilisations[loop][1] = 255;
				civilisations[loop][2] = 255;
			break;
			default:
				civilisations[loop][0] = (int)(Math.random() * 256);
				civilisations[loop][1] = (int)(Math.random() * 256);
				civilisations[loop][2] = (int)(Math.random() * 256);
			}
			
			
			civilisations[loop][7] = 10;
			civilisations[loop][3] = 1;
			civilisations[loop][8] = - 1;
			civilisations[loop][13] = (int)(Math.random() * 10);
			civilisations[loop][10] = civilisations[loop][13] + 1;
			civilisations[loop][14] = (int)(Math.random() * 10);
		}
		civilisations[0][18] = 1;
		
		//gestion des relations:
		for(int ligne = 0 ; ligne < nombreCivilisations ; ligne++){
			for(int colonne = 0 ; colonne < nombreCivilisations - 1 ; colonne++){
				if(colonne > ligne - 1){
					relations[ligne][colonne] = - 1;
				}else{
					relations[ligne][colonne] = 42;
				}
			}
		}
				
		//placement des civilisations:
		for(int loop2 = 1 ; loop2 <= nombreCivilisations ; loop2++){
			int climatCase = 0;
			int présence = 42;
			int nourriture = 0;
			int x = 0; int y = 0;
			while(climatCase == 0 || présence != 0 || nourriture < 7){
				x = (int)(Math.random() * (30 - 0));
				y = (int)(Math.random() * (50 - 0));
				climatCase = monde[x][y][0];
				présence = monde[x][y][3];
				nourriture = monde[x][y][1];
			}
			monde[x][y][3] = loop2;
		}
		
		//boucle principale:
		while(true){
			//temps d'attente:
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//addition des facteurs AL et EEM et cases des civilisations:
			for(int loop = 0 ; loop <= nombreCivilisations ; loop++){
				civilisations[loop][15] = 0; civilisations[loop][16] = 0; civilisations[loop][17] = 0;
			}
			for(int ligne = 0 ; ligne <= 29 ; ligne++){
				for(int colonne = 0 ; colonne <= 49 ; colonne++){
					civilisations[monde[ligne][colonne][3]][15] += monde[ligne][colonne][1];
					civilisations[monde[ligne][colonne][3]][16] += monde[ligne][colonne][2];
					civilisations[monde[ligne][colonne][3]][17]++;
				}
			}
			
			//calculs de gestion des civilistions:
			for(int loop = 1 ; loop <= nombreCivilisations ; loop++){
				if(civilisations[loop][18] == 0){
					//bonheur:
					if(civilisations[loop][3] > 0 && civilisations[loop][17] > 0)
						civilisations[loop][7] = (- civilisations[loop][9] + civilisations[loop][12] + (civilisations[loop][3] - civilisations[loop][9]) / 5 + civilisations[loop][5] / civilisations[loop][3]) / civilisations[loop][17];
					
					//science:
					int scienceProvisoire = civilisations[loop][10] / (10 - civilisations[loop][13]);
					if(civilisations[loop][11] < scienceProvisoire)
						civilisations[loop][11] = scienceProvisoire;
				
					//nourriture:
					civilisations[loop][12] = civilisations[loop][15] + civilisations[loop][4] / 2 + civilisations[loop][11] / 3 - civilisations[loop][3] / 4;;
					
					//population 1hab == 2AL
					civilisations[loop][19] = civilisations[loop][3];
					if((civilisations[loop][12] - (civilisations[loop][3] * 2)) >= 2){
						civilisations[loop][3]++;
					}
					if((civilisations[loop][12] - (civilisations[loop][3] * 2)) < 0){
						civilisations[loop][3]--;
					}
					
					//production:
					civilisations[loop][4] = civilisations[loop][7] / 10 + (civilisations[loop][3] - civilisations[loop][9]) + civilisations[loop][16] / 2;
					
					//économie:
					civilisations[loop][5] = civilisations[loop][16] + civilisations[loop][4] / 2 - civilisations[loop][9] / 10;
					
					//éducation:
					civilisations[loop][10] = civilisations[loop][5] / 3 + (civilisations[loop][13] + 1);
				
					//culture:
					int cultureProvisoire = civilisations[loop][7] + civilisations[loop][10] / 5;
					if(cultureProvisoire != 100){
						if(temps % (100 - civilisations[loop][7] - civilisations[loop][10] / 5) == 0){
							civilisations[loop][8]++;
						}
						if(civilisations[loop][8] < 0)
							civilisations[loop][8] = 0;
						if(civilisations[loop][8] > 9)
							civilisations[loop][8] = 9;
					}
					//influence:
					civilisations[loop][6] = civilisations[loop][5] / 3 + civilisations[loop][9] / 2;
					
					//armée:
					int politiqueProvisoire = civilisations[loop][13];
					if(civilisations[loop][20] == 1)
						politiqueProvisoire = politiqueProvisoire - 2;
					if(politiqueProvisoire < 0)
						politiqueProvisoire = 0;
					civilisations[loop][9] = civilisations[loop][3] * (9 - politiqueProvisoire) / 10;
					
					//politique (si changement)
					if(civilisations[loop][7] < civilisations[loop][8] - 4){
						civilisations[loop][13]++;
					}
					if(civilisations[loop][13] < 0)
						civilisations[loop][13] = 0;
					if(civilisations[loop][13] > 9)
						civilisations[loop][13] = 9;
					
					
					
					//décès d'une civilisation:
					if(civilisations[loop][3] <= 0 || civilisations[loop][17] <= 0){
						for(int ligne = 0 ; ligne <= 29 ; ligne++){
							for(int colonne = 0 ; colonne <= 49 ; colonne++){
								if(monde[ligne][colonne][3] == loop){
									monde[ligne][colonne][3] = 0;
								}
							}
						}
						
						civilisations[loop][18] = 1;
						civilisations[loop][20] = 0;
					}
				}
			}
			
			
			
			
			
			//gestion des évènements:
			//expension:
			for(int loop = 1 ; loop <= nombreCivilisations ; loop++){
				if(civilisations[loop][18] == 0){
					
					if(civilisations[loop][3] == civilisations[loop][19] || temps % (10 - civilisations[loop][14]) == 0){
						int totalZone = -1;
						int x2 = -1; int y2 = -1;
						for(int ligne = 0 ; ligne <= 29 ; ligne++){
							for(int colonne = 0 ; colonne <= 49 ; colonne++){
								if(monde[ligne][colonne][3] == loop){
									for(int loop2 = 0 ; loop2 <  4 ; loop2++){
										int x = ligne;
										int y = colonne;
										if(loop2 == 0){
											x--;
											if(x == -1){
												x = 29;
											}
										}
										if(loop2 == 1){
											x++;
											if(x == 30){
												x = 0;
											}
										}
										if(loop2 == 2){
											y--;
											if(y == -1){
												y = 49;
											}
										}
										if(loop2 == 3){
											y++;
											if(y == 50){
												y = 0;
											}
										}
										if(monde[x][y][3] == 0 && monde[x][y][0] != 0){
											int totalZone2 = monde[x][y][1] + monde[x][y][2];
											if(totalZone2 > totalZone){
												totalZone = totalZone2;
												x2 = x;
												y2 = y;
											}
										}
									}
								}
							}
						}
						if(totalZone != -1){
							monde[x2][y2][3] = loop;
						}
					}
					
				}
			}
			
			//tableau des batailles:
			//[x][y]  []--> 0-> il y a une bataille si ==1   1->coord x de l'ennemi 	2->y	3->nb soldats	4->nb soldats sc ou eem
			int batailles[][][] =  new int[30][50][5];
			
			
			//resolutions de front, alliance:
			for(int loop = 1 ; loop <= nombreCivilisations ; loop++){
				civilisations[loop][20] = 0;
				civilisations[loop][21] = 0;
			}
			for(int première = 1 ; première <= nombreCivilisations ; première++){
				for(int deuxième = première + 1 ; deuxième <= nombreCivilisations ; deuxième++){
					
					if(civilisations[première][18] == 0 && civilisations[deuxième][18] == 0){
						
						//définition des fronts:
						if(relations[première - 1][deuxième - 2] == 0){
							
							civilisations[première][20] = 1;
							civilisations[deuxième][20] = 1;
							
							//association des paires de cases pour faire une bataille
							for(int ligne = 0 ; ligne <= 29 ; ligne++){
								for(int colonne = 0 ; colonne <= 49 ; colonne++){
									if(monde[ligne][colonne][3] == première){
										for(int loopFront = 0 ; loopFront < 4 ; loopFront++){
											int x = ligne; int y = colonne;
											if(loopFront == 0){
												x = ligne - 1;
												if(x == -1) x = 29;
											}
											if(loopFront == 1){
												x = ligne + 1;
												if(x == 30) x = 0;
											}
											if(loopFront == 2){
												y = colonne + 1;
												if(y == 50) y = 0;
											}
											if(loopFront == 3){
												y = colonne - 1;
												if(y == -1) y = 49;
											}
											if(monde[x][y][3] == deuxième && batailles[x][y][0] != 1 && batailles[ligne][colonne][0] != 1){
												civilisations[première][21]++;civilisations[deuxième][21]++;
												batailles[ligne][colonne][0] = 1; batailles[x][y][0] = 1;
												batailles[ligne][colonne][1] = x; batailles[x][y][1] = ligne;
												batailles[ligne][colonne][2] = y; batailles[x][y][2] = colonne;
												loopFront = 42;
											}
										}
									}
								}
							}
							
						}
						
						
						//aliance:
						if(relations[première - 1][deuxième - 2] == 9){
							//couleur:
							int RMoy = ((civilisations[première][0] + civilisations[deuxième][0]) / 2);
							int GMoy = ((civilisations[première][1] + civilisations[deuxième][1]) / 2);
							int BMoy = ((civilisations[première][2] + civilisations[deuxième][2]) / 2);
							civilisations[première][0] = RMoy; civilisations[première][1] = GMoy; civilisations[première][2] = BMoy;
							//territoire:
							for(int ligne = 0 ; ligne <= 29 ; ligne++){
								for(int colonne = 0 ; colonne <= 49 ; colonne++){
									if(monde[ligne][colonne][3] == deuxième){
										monde[ligne][colonne][3] = première;
									}
								}
							}
							//facteurs:
							civilisations[première][3] = civilisations[première][3] + civilisations[deuxième][3];
							civilisations[première][8] = ((civilisations[première][8] + civilisations[deuxième][8]) / 2);
							civilisations[première][13] = ((civilisations[première][13] + civilisations[deuxième][13]) / 2);
							civilisations[première][14] = ((civilisations[première][14] + civilisations[deuxième][14]) / 2);
							//relations:
							for(int loop = 1 ; loop <= nombreCivilisations ; loop++){
								boolean contactPremière = true; boolean contactDeuxième = true;
								if(loop != première && loop != deuxième){
									if(loop > première && loop < deuxième){
										if(relations[première - 1][loop - 2] == - 1){relations[première - 1][loop - 2] = 5; contactPremière = false;}
										if(relations[loop - 1][deuxième - 2] == - 1){relations[loop - 1][deuxième - 2] = 5; contactDeuxième = false;}
										if(contactPremière || contactDeuxième){
										relations[première - 1][loop - 2] = (relations[première - 1][loop - 2] + relations[loop - 1][deuxième - 2]) / 2;
										}else{
											relations[première - 1][loop - 2] = - 1; relations[loop - 1][deuxième - 2] = - 1;
										}
									}else{
										if(loop > deuxième){
											if(relations[première - 1][loop - 2] == - 1){relations[première - 1][loop - 2] = 5; contactPremière = false;}
											if(relations[deuxième - 1][loop - 2] == - 1){relations[deuxième - 1][loop - 2] = 5; contactDeuxième = false;}
											if(contactPremière || contactDeuxième){
											relations[première - 1][loop - 2] = (relations[première - 1][loop - 2] + relations[deuxième - 1][loop - 2]) / 2;
											}else{
												relations[première - 1][loop - 2] = - 1; relations[deuxième - 1][loop - 2] = - 1;
											}
										}else{
											if(relations[loop - 1][première - 2] == - 1){relations[loop - 1][première - 2] = 5; contactPremière = false;}
											if(relations[loop - 1][deuxième - 2] == - 1){relations[loop - 1][deuxième - 2] = 5; contactDeuxième = false;}
											if(contactPremière || contactDeuxième){
											relations[loop - 1][première - 2] = (relations[loop - 1][première - 2] + relations[loop - 1][deuxième - 2]) / 2;
											}else{
												relations[loop - 1][première - 2] = - 1; relations[loop - 1][deuxième - 2] = - 1;
											}
										}
									}
								}
							}
							//suppresion:
							civilisations[deuxième][18] = 2;
						}
						
						
						
						
					}
				}
			}
			
			
			//placement soldats:
			for(int loop = 1 ; loop <= nombreCivilisations ; loop++){
				if(civilisations[loop][20] == 1 && civilisations[loop][18] == 0 && civilisations[loop][21] != 0){
					int resteSoldats = civilisations[loop][9] % civilisations[loop][21];
					int resteSoldatsSc = civilisations[loop][11] % civilisations[loop][21];
					int nbSoldats = (civilisations[loop][9] - resteSoldats) / civilisations[loop][21];
					int nbSoldatsSc = (civilisations[loop][11] - resteSoldatsSc) / civilisations[loop][21];
					for(int ligne = 0 ; ligne <= 29 ; ligne++){
						for(int colonne = 0 ; colonne <= 49 ; colonne++){
							if(monde[ligne][colonne][3] == loop){
								if(batailles[ligne][colonne][0] == 1){
									batailles[ligne][colonne][3] = nbSoldats;
									if(resteSoldats > 0){batailles[ligne][colonne][3]++; resteSoldats--; }
									batailles[ligne][colonne][4] = nbSoldatsSc + monde[ligne][colonne][2];
									if(resteSoldatsSc > 0){batailles[ligne][colonne][4]++; resteSoldatsSc--; }
										
								}
							}
						}
					}
					
				}
			}
			
			
			//resolution des batailles:
			for(int ligne = 0 ; ligne <= 29 ; ligne++){
				for(int colonne = 0 ; colonne <= 49 ; colonne++){
					if(batailles[ligne][colonne][0] == 1){
						int nbSoldatsTotalCase = batailles[ligne][colonne][3] + batailles[ligne][colonne][4];
						int nbSoldatsTotalEnnemi = batailles[batailles[ligne][colonne][1]][batailles[ligne][colonne][2]][3] + batailles[batailles[ligne][colonne][1]][batailles[ligne][colonne][2]][4];
						if(nbSoldatsTotalCase > nbSoldatsTotalEnnemi){
							monde[batailles[ligne][colonne][1]][batailles[ligne][colonne][2]][3] = monde[ligne][colonne][3];
							civilisations[monde[batailles[ligne][colonne][1]][batailles[ligne][colonne][2]][3]][3] -= batailles[batailles[ligne][colonne][1]][batailles[ligne][colonne][2]][3];
							int soldatsSc = batailles[ligne][colonne][4];
							if(soldatsSc < nbSoldatsTotalEnnemi)
								civilisations[monde[ligne][colonne][3]][3] -= nbSoldatsTotalEnnemi - soldatsSc;
						}else{
							if(nbSoldatsTotalCase < nbSoldatsTotalEnnemi){
								monde[ligne][colonne][3] = monde[batailles[ligne][colonne][1]][batailles[ligne][colonne][2]][3];
								civilisations[monde[ligne][colonne][3]][3] -= batailles[ligne][colonne][3];
								int soldatsSc = batailles[batailles[ligne][colonne][1]][batailles[ligne][colonne][2]][4];
								if(soldatsSc < nbSoldatsTotalCase)
									civilisations[monde[batailles[ligne][colonne][1]][batailles[ligne][colonne][2]][3]][3] -= nbSoldatsTotalCase - soldatsSc;
							}else{
								civilisations[monde[ligne][colonne][3]][3] -= batailles[ligne][colonne][3];
								civilisations[monde[batailles[ligne][colonne][1]][batailles[ligne][colonne][2]][3]][3] -= batailles[batailles[ligne][colonne][1]][batailles[ligne][colonne][2]][3];
							}
						}
						
						batailles[ligne][colonne][0] = 0;
						batailles[batailles[ligne][colonne][1]][batailles[ligne][colonne][2]][0] = 0;
					}
				}
			}
			
			
			//gestion des relations:
			for(int première = 1 ; première <= nombreCivilisations ; première++){
				for(int deuxième = première + 1 ; deuxième <= nombreCivilisations ; deuxième++){
					
					if(relations[première - 1][deuxième - 2] != -1 && civilisations[première][18] == 0 && civilisations[deuxième][18] == 0){
						
						//initialisations des diférances
						double difInfl = (double)(civilisations[première][6]) - (double)(civilisations[deuxième][6]);
						if(difInfl < 0) difInfl = difInfl * - 1;
						double difEco = (double)(civilisations[première][5]) - (double)(civilisations[deuxième][5]);
						if(difEco < 0) difEco = difEco * - 1;
						double difPol = (double)(civilisations[première][13]) - (double)(civilisations[deuxième][13]);
						if(difPol < 0) difPol = difPol * - 1;
						
						//relations entre 1 et 8
							if(relationsProv[première - 1][deuxième - 2] >= 1 && relationsProv[première - 1][deuxième - 2] <= 8){
								
								//par politique
								if(difPol != 0){
									relationsProv[première - 1][deuxième - 2] -= difPol / 100;
								}else{
										relationsProv[première - 1][deuxième - 2] += 0.01;
								}
								
								//par extension
								relationsProv[première - 1][deuxième - 2] -= (civilisations[première][14] + civilisations[deuxième][14]) / 100;
								
								
						
						//limites à 1 ou 8
							//pour 1
								if(relationsProv[première - 1][deuxième - 2] < 1)
									relationsProv[première - 1][deuxième - 2] = 1;
							//pour 8
								if(relationsProv[première - 1][deuxième - 2] > 8)
									relationsProv[première - 1][deuxième - 2] = 8;
								
							}
						
						int guerreProvisoire = 	relations[première - 1][deuxième - 2];
						//adaptation Provisoire -> tableau
							relations[première - 1][deuxième - 2] = (int)(relationsProv[première - 1][deuxième - 2]);
							
							
						//déclaration de guerre
							if(relations[première - 1][deuxième - 2] <= 1){
								//valeur de la proportion de population pour début et fin de guerre (a / 10)
								double a = 4;
								//pop[1] et [2] > a% pop max
								if((civilisations[première][3] > (civilisations[première][12] * 1 / 2) * a / 10) && (civilisations[deuxième][3] > (civilisations[deuxième][12] * 1 / 2) * a / 10)){
									if((difEco > 200) || (difPol > 3)){
										relations[première - 1][deuxième - 2] = 0;
									}
								}else{
									//dons des fronts
									if(guerreProvisoire == 0){
										int gagnante; int perdante;
										if(civilisations[première][6] > civilisations[deuxième][6]){
											gagnante = première; perdante = deuxième;
										}else{
											gagnante = deuxième; perdante = première;
										}
										if(civilisations[première][6] != civilisations[deuxième][6]){
											int[][] marquage = new int[30][50];
											for(int ligne = 0 ; ligne <= 29 ; ligne++){
												for(int colonne = 0 ; colonne <= 49 ; colonne++){
													if(monde[ligne][colonne][3] == gagnante && marquage[ligne][colonne] == 0){
														for(int loopFront = 0 ; loopFront < 4 ; loopFront++){
															int x = ligne; int y = colonne;
															if(loopFront == 0){
																x = ligne - 1;
																if(x == -1) x = 29;
															}
															if(loopFront == 1){
																x = ligne + 1;
																if(x == 30) x = 0;
															}
															if(loopFront == 2){
																y = colonne + 1;
																if(y == 50) y = 0;
															}
															if(loopFront == 3){
																y = colonne - 1;
																if(y == -1) y = 49;
															}
															if(monde[x][y][3] == perdante && marquage[x][y] == 0){
																marquage[x][y] = 1; marquage[ligne][colonne] = 1;
																monde[x][y][3] = gagnante;
																loopFront = 42;
															}
														}
													}
												}
											}
										}
										}
									//arret de guerre
									relations[première - 1][deuxième - 2] = 1;
								}
							}
						
						
						//aliance
							if(relations[première - 1][deuxième - 2] == 8){
								if((difPol == 0) && (civilisations[première][6] < temps && civilisations[deuxième][6] < temps)){
									relations[première - 1][deuxième - 2] = 9;
								}
							}
						
						}
					}
				}
			
			//génération et vérification des relations:
			for(int ligne = 0 ; ligne <= 29 ; ligne++){
				for(int colonne = 0 ; colonne <= 49 ; colonne++){
					if(monde[ligne][colonne][3] > 0){
						for(int loop = 0 ; loop < 4 ; loop++){
							int x = ligne; int y = colonne;
							if(loop == 0){
								x--;
								if(x == -1){
									x = 29;
								}
							}
							if(loop == 1){
								x++;
								if(x == 30){
									x = 0;
								}
							}
							if(loop == 2){
								y--;
								if(y == -1){
									y = 49;
								}
							}
							if(loop == 3){
								y++;
								if(y == 50){
									y = 0;
								}
							}
							if(monde[ligne][colonne][3] != monde[x][y][3] && monde[x][y][3] > 0){
								int première = monde[ligne][colonne][3]; int deuxième = monde[x][y][3];
								if(première > deuxième){
									int provisoire = première;
									première = deuxième;
									deuxième = provisoire;
								}
								if(relations[première - 1][deuxième - 2] == - 1){
									relations[première - 1][deuxième - 2] = 5;
									relationsProv[première - 1][deuxième - 2] = 5;
								}
							}
						}
					}
				}
			}
			
			repaint();
			temps = temps + 1L;
		}
		
	}
	
	
	//Actualisation de l'image:
	public class Panneau extends JPanel{
		public void paintComponent(Graphics g){
			g.setColor(new Color(100, 100, 100));
			
			//carte
			for(int ligne = 0 ; ligne <= 29 ; ligne++){
				for(int colonne = 0 ; colonne <= 49 ; colonne++){
					
					if(monde[ligne][colonne][0] == 0){
						g.setColor(Color.black);
					}else{
						g.setColor(new Color(civilisations[monde[ligne][colonne][3]][0], civilisations[monde[ligne][colonne][3]][1], civilisations[monde[ligne][colonne][3]][2]));
					}
					
					
					g.fillRect(colonne * 10, ligne * 10, 10, 10);
					g.setColor(Color.black);
					g.drawRect(colonne * 10, ligne * 10, 10, 10);
					
					
				}
			}
			
			//tableau de statistiques:
			Font font = new Font("Arial", Font.BOLD, 32);
			g.setFont(font); g.setColor(Color.red);
			g.drawString("Statistiques", 800, 30);
			g.drawString("Relations", 40, 440);
			g.setColor(Color.black);
			Font font2 = new Font("Arial", Font.BOLD, 10);
			g.setFont(font2);
			g.drawString("Date :", 520, 10);
			g.drawString(String.valueOf(temps), 550, 10);
			g.drawString("ID", 520, 45);
			g.drawString("Pop", 570, 45);
			g.drawString("Prod", 620, 45);
			g.drawString("Eco", 670, 45);
			g.drawString("Inf", 720, 45);
			g.drawString("Bonh", 770, 45);
			g.drawString("Cult", 820, 45);
			g.drawString("Arm", 870, 45);
			g.drawString("Edu", 920, 45);
			g.drawString("Sci", 970, 45);
			g.drawString("Nour", 1020, 45);
			g.drawString("Pol", 1070, 45);
			g.drawString("Exten", 1115, 45);
			g.drawString("AL", 1170, 45);
			g.drawString("EEM", 1220, 45);
			g.drawString("Cases", 1265, 45);
			for(int ligne = 0 ; ligne < nombreCivilisations ; ligne++){
				for(int colonne = 0 ; colonne <= 15 ; colonne++){
					g.drawRect(colonne * 50 + 502, ligne * 20 + 50, 50, 20);
				}
				g.drawString(String.valueOf(ligne + 1), 505, ligne * 20 + 68);
			}
			for(int ligne = 0 ; ligne < nombreCivilisations ; ligne++){
				for(int colonne = 0 ; colonne <= 14 ; colonne++){
					g.drawString(String.valueOf(civilisations[ligne + 1][colonne + 3]), colonne * 50 + 555, ligne * 20 + 68);
				}
			}
			for(int loop = 1 ; loop <= nombreCivilisations ; loop++){
				g.setColor(new Color(civilisations[loop][0], civilisations[loop][1], civilisations[loop][2]));
				g.fillRect(525, loop * 20 + 38, 10, 10);
				if(civilisations[loop][18] == 1){
					g.setColor(Color.red);
					g.drawLine(502, loop * 20 + 42, 1300, loop * 20 + 42);
				}
				if(civilisations[loop][18] == 2){
					g.setColor(Color.blue);
					g.drawLine(502, loop * 20 + 42, 1300, loop * 20 + 42);
				}
			}
			
			
			//tableau des relations:
			g.setColor(Color.black);
			for(int loop = 1 ; loop <= nombreCivilisations ; loop++){
				if(loop != nombreCivilisations)
					g.drawString(String.valueOf(loop), 10, 450 + 12 * loop);
				if(loop != 1)
					g.drawString(String.valueOf(loop), 10 + 12 * loop, 450);
			}
			for(int ligne = 1 ; ligne <= nombreCivilisations ; ligne++){
				for(int colonne = 2 ; colonne <= nombreCivilisations ; colonne++){
					if(relations[ligne - 1][colonne - 2] != 42){
						g.drawRect(7 + colonne * 12, 439 + ligne * 12, 12, 12);
						if(civilisations[ligne][18] == 0 && civilisations[colonne][18] == 0){
							g.drawString(String.valueOf(relations[ligne - 1][colonne - 2]), 8 + colonne * 12, 450 + ligne * 12);
						}
						else{
							g.drawString("x", 8 + colonne * 12, 450 + ligne * 12);
						}
					}
				}
			}
			
		}
	}
	
	
	//DEPART      main:      
	public static void main(String[] args) {
        //appel de "JeuDesCivilisations" la classe principale:
		new JeuDesCivilisations();
    }
}
