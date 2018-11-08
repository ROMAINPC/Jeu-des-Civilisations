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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


public class JeuDesCivilisations extends JFrame{
	//VERSION FINALE
		//Variables:
		public int nombreCivilisations;
		public boolean aleatoire;
		public long temps;
		//tableau de génération de la carte:
		//0=climat    1=ressources AL    2=ressources EEM   3=appartenance de la case
		public int[][][] monde;
		//tableau des civilisations:
		//0->R	1->G	2->B	3->population	4->production	5->économie	6->influence
		//7->bonheur	8->culture	9->armée	10->éducation	11->science
		//12->nourriture	13->politique	14->politique territoriale	15->AL total	16->EEM total
		//17->cases possédées	18->1=décès2=alliance	19-> pop provisoire	
		//20->est en guerre avec qq`(1=oui)		21->nb batailles à faire	NB:int[0][x]->case n'appartenant à personne
		public int[][] civilisations;
		//tableau des relations:
		public int[][] relations;
		//tableau des relations provisoires:
		public double[][] relationsProv;
		
		
		public int vitesse = 200;
		public boolean animation = true;
		public boolean première = true;
		
		public JButton play = new JButton("Pause");
		public JButton reset = new JButton("Nouveau");
		
		public boolean scenario1;
		public boolean scenario2;
		public boolean scenario3;
		public boolean scenario4;
		public boolean scenario5;
		public boolean scenario6;
		public boolean scenario7;
		public boolean scenario8;
		public boolean scenario9;
		public boolean scenario10;
		
		//aleatoire
		public int[] tempRandom;
		public int[] saveNamNam;
		public int[] savePol;
		public int[] savePop;
		public int[] saveEco;
		public int[] eventRandom;
		//peste
		public int presEpidemie;
		public int[] savepop;
		public int powEpidemie;
		public int rangeEpidemie;
		public int[][] epidemie;
		
		//création de la Fenetre:
		public JeuDesCivilisations(){
			//paramétrage:
			this.setTitle("Jeu des Civilisations");
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    this.setResizable(false);
			this.setSize(1350, 720);
			this.setLocationRelativeTo(null);
			this.setBackground(new Color(100, 100, 100));
			this.setIconImage(new ImageIcon(getClass().getResource("/img/icone.png")).getImage());
			//définition du content pane:
			Panneau container = new Panneau();
			this.setContentPane(container);
			
			//boutons:
			//placement du container boutons et vitesse:
			JPanel boutons = new JPanel();
			boutons.setBackground(new Color(0, 0, 100, 0));
			JPanel vitesse = new JPanel();
			vitesse.setBackground(new Color(0, 100, 0, 0));
			int x = 15; int y = 3;
			this.setLayout(new GridLayout(x, y));
			for(int loop = 1 ; loop <= x * y ; loop++){
				if(loop != 22){
					if(loop == 25){
						add(vitesse);
					}else{
						JPanel panel = new JPanel();
						panel.setBackground(new Color(0, 0, 0, 0));
						add(panel);
					}
				}else{
					add(boutons);
				}
			}
			//placement des boutons:
			play.addActionListener(new PausePlay());
			reset.addActionListener(new Reinitialisation());
			JButton save = new JButton("Sauvegarde"); save.addActionListener(new Sauvegarde());
			JRadioButton vitesse10 = new JRadioButton("très rapide");
			JRadioButton vitesse50 = new JRadioButton("rapide");
			JRadioButton vitesse100 = new JRadioButton("lent");
			JRadioButton vitesse200 = new JRadioButton("très lent");
			vitesse10.addActionListener(new Changement()); vitesse50.addActionListener(new Changement()); vitesse100.addActionListener(new Changement()); vitesse200.addActionListener(new Changement());			
			ButtonGroup bg = new ButtonGroup(); bg.add(vitesse10); bg.add(vitesse50); bg.add(vitesse100); bg.add(vitesse200);
			boutons.setLayout(new BorderLayout());
			boutons.add(reset, BorderLayout.NORTH);
			boutons.add(play, BorderLayout.WEST);
			boutons.add(save, BorderLayout.EAST);
			vitesse.add(vitesse10);
			vitesse.add(vitesse50);
			vitesse.add(vitesse100);
			vitesse.add(vitesse200);
			vitesse200.setSelected(true);
			
			//visibilité:
			this.setVisible(true);
			
			//debut du programme:
			repaint();
			JOptionPane.showMessageDialog(null, "Bienvenue dans le Jeu Des Civilisations !\n Ce logiciel fut imaginé et conçu par:\n          ROMAINPC\n          Nono13064\n          theroxas898\n Remerciements : à Java (Oracle) et au logiciel Eclipse.\nCette application fut réalisée dans le cadre des TPE.", "Crédits", JOptionPane.INFORMATION_MESSAGE);
			Generation();
			while(true){
				Go();
			}
		}
		public void Generation(){
			//PARAMETRAGE DE LA PARTIE:
			//chargement ou pas?
			boolean chargement = false;
			if(!première){
			int resultat = JOptionPane.showConfirmDialog(null, "Vous allez créer une nouvelle partie. Voulez-vous charger une carte que vous possédez ?", "Paramétrage : génération aléatoire ou chargement carte", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(resultat == 0){
				//charger une map:
				chargement = true; String nom = null;
				while(nom == null)
					nom = JOptionPane.showInputDialog(null, "Pour charger une carte placez le fichier .txt dans le même dossier que l'application. \n Puis entrez ici son nom (sans le .txt derrière):", "Chargement d'une carte", JOptionPane.QUESTION_MESSAGE);
				Chargement(nom);
				
			}}
			//aléatoire ou pas?
			aleatoire = false;
			int resultatAlea = JOptionPane.showConfirmDialog(null, "Voulez-vous autoriser les évènements aléatoires ?\nCoups d'états/Guerre civile/Crise économique/Famine/Catastrophe naturelle(noté par CE GC C F et CN) et Epidémie(notée par des x)\nNB: si vous chargez une carte ayant l'alétoire, il est conseillé de le remettre.", "Paramétrage : évènements aléatoires", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(resultatAlea == 0) aleatoire = true;
			
			scenario1 = false;
			scenario2 = false;
			scenario3 = false;
			scenario4 = false;
			scenario5 = false;
			scenario6 = false;
			scenario7 = false;
			scenario8 = false;
			scenario9 = false;
			scenario10 = false;
			//scénario?
			new Scenarios(null, "Paramétrage : scénarios", true);
			
			
			
			if(!chargement){
			if(première) première = false;
			//nombre de civilisations:
			String[] tableau = {"1","2","3","4","5","6","7","8","9","10","15","20","25","50","100"};
			String resultat = (String)JOptionPane.showInputDialog(null, "Veuillez sélectionner un nombre de civilisations:", "Paramétrage : Nombre de civilisations", JOptionPane.INFORMATION_MESSAGE, null, tableau, tableau[9]);
			if(resultat == null) nombreCivilisations = 10;
			else nombreCivilisations = Integer.parseInt(resultat);
			
			//Initialisation des varaibles:
			
			
			temps = 0L;
			monde = new int[30][50][4];
			civilisations= new int[nombreCivilisations + 1][22];
			relations = new int[nombreCivilisations][nombreCivilisations - 1];
			relationsProv = new double[nombreCivilisations][nombreCivilisations - 1];
			tempRandom = new int[nombreCivilisations] ;
			saveNamNam = new int[nombreCivilisations];
			savePol = new int[nombreCivilisations];
			savePop = new int[nombreCivilisations];
			saveEco = new int[nombreCivilisations];
			eventRandom = new int[nombreCivilisations];
			presEpidemie = 0;
			savepop = new int[nombreCivilisations];
			epidemie = new int[30][50];
			//génération de la carte:
				for(int ligne = 0 ; ligne <= 29 ; ligne++){
					for(int colonne = 0 ; colonne <= 49 ; colonne++){
						if(scenario10) monde[ligne][colonne][0] = (int)(Math.random() * 3);
						else monde[ligne][colonne][0] = (int)(Math.random() * 7);
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
				
				//save auto
				Sauvegarde("auto");
				
			}
			JOptionPane.showMessageDialog(null, "Il vous suffit maintenant de cliquer sur \"Reprendre\" pout lancer l'animation", "Info", JOptionPane.INFORMATION_MESSAGE);
			repaint();
		}
		
		
		//fonction principale:
		public void Go(){
			//boucle principale:
			while(animation){
				//temps d'attente:
				try {
					Thread.sleep(vitesse);
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
						if(civilisations[loop][3] > 0 && civilisations[loop][17] > 0){
							if(scenario6) civilisations[loop][7] = civilisations[loop][7] + 15;
							civilisations[loop][7] = (- civilisations[loop][9] + civilisations[loop][12] + (civilisations[loop][3] - civilisations[loop][9]) / 5 + civilisations[loop][5] / civilisations[loop][3]) / civilisations[loop][17];
							if(scenario6) civilisations[loop][7] = civilisations[loop][7] - 15;
						}
						
						//science:
						if(scenario3)civilisations[loop][11] = civilisations[loop][11] / 10;
						if(scenario4)civilisations[loop][11] = civilisations[loop][11] * 10;
						int scienceProvisoire = civilisations[loop][10] / (10 - civilisations[loop][13]);
						if(civilisations[loop][11] < scienceProvisoire)
							civilisations[loop][11] = scienceProvisoire;
						if(scenario3)civilisations[loop][11] = civilisations[loop][11] * 10;
						if(scenario4)civilisations[loop][11] = civilisations[loop][11] / 10;
					
						//nourriture:
						if(eventRandom[loop - 1] != 4)
							civilisations[loop][12] = civilisations[loop][15] + civilisations[loop][4] / 2 + civilisations[loop][11] / 3 - civilisations[loop][3] / 4;;
						
						//population 1hab == 2AL
						civilisations[loop][19] = civilisations[loop][3];
						int nourrNecessaire = 2;
						if(scenario1) nourrNecessaire = 1;
						if(scenario2) nourrNecessaire = 3;
						if((civilisations[loop][12] - (civilisations[loop][3] * nourrNecessaire)) >= nourrNecessaire){
							civilisations[loop][3]++;
						}
						if((civilisations[loop][12] - (civilisations[loop][3] * nourrNecessaire)) < 0){
							civilisations[loop][3]--;
						}
						
						//production:
						civilisations[loop][4] = civilisations[loop][7] / 10 + (civilisations[loop][3] - civilisations[loop][9]) + civilisations[loop][16] / 2;
						
						//économie:
						if(eventRandom[loop - 1] != 3){
							if(scenario7) civilisations[loop][5] = civilisations[loop][5] * 10;
							civilisations[loop][5] = civilisations[loop][16] + civilisations[loop][4] / 2 - civilisations[loop][9] / 10;
							if(scenario7) civilisations[loop][5] = civilisations[loop][5] / 10;
						}
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
				
				
				//évènements aléatoires:
				int valRand = 0;
				//1=coup d'état
				//2=Guerre civile
				//3=Crise éco
				//4=Famine
				//5=Catastrophe naturelle(autres que peste)
				if(aleatoire){
					for(int loop = 1 ; loop <= nombreCivilisations ; loop++){
						if(civilisations[loop][18] == 0){
						if(eventRandom[loop - 1] == 0){
							if(civilisations[loop][9] >=  civilisations[loop][3]/2){
								valRand = (int) (Math.random() * ( 200 - 0 ));
								if(valRand == 1){
									eventRandom[loop - 1] = 1;
									savePol[loop - 1] = (int) (Math.random() * ( 3 - 0 ));
									civilisations[loop][13] = savePol[loop - 1];
									tempRandom[loop - 1] = (int) (5 + (Math.random() * ( 50 - 5 )));
								}
							}
							else if(civilisations[loop][7] <= 1){
								valRand = (int) (Math.random() * ( 2 - 0 ));
								if(valRand == 1){
									eventRandom[loop - 1] = 2;
									savePop[loop-1] = civilisations[loop][3];
									tempRandom[loop-1] =(int) (1+(Math.random() * ( 10 - 1 )));
								}
							}
							else if(3*civilisations[loop][11]>civilisations[loop][5]){
								valRand = (int) (Math.random() * ( 500 - 0 ));
								if(valRand == 1){
									eventRandom[loop-1] = 3;
									tempRandom[loop-1] = (int)(1+(Math.random() * ( 50 - 1 )));
									saveEco[loop-1] = civilisations[loop][5];
								}
							}
							else{
								valRand = (int) (Math.random() * ( 1000 - 0 ));
								if(valRand == 500){
									eventRandom[loop - 1] = 4;
									tempRandom[loop-1] = (int)(1+(Math.random() * ( 100 - 1 )));
									saveNamNam[loop-1] = civilisations[loop][12];
								}
								else if(valRand == 700){
									eventRandom[loop - 1] = 5;
									tempRandom[loop - 1] = (int)(1+(Math.random() * ( 5 - 1 )));
								}
							}
						}
						else if(eventRandom[loop-1] == 1){
							tempRandom[loop-1]--;
							civilisations[loop][13] = savePol[loop-1];
							if(tempRandom[loop-1] <= 0){
								eventRandom[loop-1] = 0;
							}
						}
						else if(eventRandom[loop-1] == 2){
							tempRandom[loop-1]--;
							if(civilisations[loop][3] <= savePop[loop-1]/2){
								tempRandom[loop-1] = 0;
							}
							civilisations[loop][3] = civilisations[loop][3] - ((int)(1 + (Math.random() * ( (civilisations[loop][3]/10) - 1 ))));
							if(tempRandom[loop-1] <= 0){
								eventRandom[loop-1] = 0;
							}
						}
						else if(eventRandom[loop-1] == 3){
							tempRandom[loop-1]--;
							civilisations[loop][5] = saveEco[loop-1]/2;
							if(tempRandom[loop-1] == 0){
								eventRandom[loop-1] = 0;
							}
						}
						else if(eventRandom[loop-1] == 4){
							tempRandom[loop-1]--;
							civilisations[loop][12] = saveNamNam[loop-1]/2;
							if(tempRandom[loop-1] == 0){
								eventRandom[loop-1] = 0;
							}
						}
						else if(eventRandom[loop-1] == 5){
							tempRandom[loop-1]--;
							civilisations[loop][3] = civilisations[loop][3] - ((int)(1+Math.random() * ( civilisations[loop][3]/10 - 1 )));
							if(tempRandom[loop-1] <= 0){
								eventRandom[loop-1] = 0;
							}
						}
						}
					}
				}
				
				//épidemie
				if(aleatoire){
					int yEpidemie;
					int xEpidemie;
					if(presEpidemie == 1){
						rangeEpidemie--;
						for(int loopy = 0 ; loopy <= 29 ; loopy ++){
							for(int loopx = 0 ; loopx <= 49 ; loopx++){
								if(epidemie[loopy][loopx] != 0 && epidemie[loopy][loopx] <= 3){
									epidemie[loopy][loopx]++;
								}
							}
						}
						for(int loopy = 0 ; loopy <= 29 ; loopy ++){
							for(int loopx = 0 ; loopx <= 49 ; loopx++){
								if(epidemie[loopy][loopx] == 2){
									if(loopy == 29){
										if(epidemie[0][loopx] == 0 && monde[0][loopx][3] != 0){
											epidemie[0][loopx] = 1;
										}
									}
									else{
										if(epidemie[loopy+1][loopx] == 0 && monde[loopy+1][loopx][3] != 0){
											epidemie[loopy+1][loopx] = 1;
										}
									}
									if(loopx == 49){
										if(epidemie[loopy][0] == 0 && monde[loopy][0][3] != 0){
											epidemie[loopy][0] = 1;
										}
									}
									else{
										if(epidemie[loopy][loopx+1] == 0 && monde[loopy][loopx+1][3] != 0){
											epidemie[loopy][loopx+1] = 1;
										}
									}
									if(loopy == 0){
										if(epidemie[29][loopx] == 0 && monde[29][loopx][3] != 0){
											epidemie[29][loopx] = 1;
										}
									}
									else{
										if(epidemie[loopy-1][loopx] == 0 && monde[loopy-1][loopx][3] != 0){
											epidemie[loopy-1][loopx] = 1;
										}
									}
									if(loopx == 0){
										if(epidemie[loopy][49] == 0 && monde[loopy][49][3] != 0){
											epidemie[loopy][49] = 1;
										}
									}
									else{
										if(epidemie[loopy][loopx-1] == 0 && monde[loopy][loopx-1][3] != 0){
											epidemie[loopy][loopx-1] = 1;
										}
									}
								}
								else if(epidemie[loopy][loopx] == 3 && monde[loopy][loopx][3] != 0){
									civilisations[monde[loopy][loopx][3]][3] = civilisations[monde[loopy][loopx][3]][3] - ((savepop[monde[loopy][loopx][3] - 1] / civilisations[monde[loopy][loopx][3]][17]) / powEpidemie);
								}
							}
						}
						if(rangeEpidemie <= 0){
							presEpidemie = 0;
							for(int loopy = 0 ; loopy <= 29 ; loopy ++){
								for(int loopx = 0 ; loopx <= 49 ; loopx++){
									epidemie[loopy][loopx] = 0;
								}
							}
						}
					}
					else if((int)(Math.random() * 500) == 42){
						presEpidemie = 1;
						do{
							yEpidemie = (int) (Math.random() * 30);
							xEpidemie = (int) (Math.random() * 50);
						}while(monde[yEpidemie][xEpidemie][3] == 0);
						epidemie[yEpidemie][xEpidemie] = 1;
						powEpidemie = (int) (Math.random() * 2) + 1;
						rangeEpidemie = (int) ((Math.random() * ( 50 - 0 )));
						for(int loop = 1 ; loop <= nombreCivilisations ; loop ++){
							savepop[loop - 1] = civilisations[loop][3];
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
							if(relations[première - 1][deuxième - 2] == 9 && !scenario8){
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
				
				
				
				
				//gestion des relations
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
									if(scenario5) a = 1;
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
										if(scenario9){
											relations[première - 1][deuxième - 2] = 1;
											relationsProv[première - 1][deuxième - 2] = 1;
										}
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
				try{
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
						if(epidemie[ligne][colonne] != 0){
							g.drawLine(colonne * 10, ligne * 10, colonne * 10 + 10, ligne * 10 + 10);
							g.drawLine(colonne * 10 + 10, ligne * 10, colonne * 10, ligne * 10 + 10);
						}
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
				g.drawString("/!\\ le bouton pause peut influer sur la simulation /!\\", 10, 410);
				//aléatoires
				if(aleatoire) g.drawString("Evènements aléatoires : actifs", 520, 25);
				else g.drawString("Evènements aléatoires : inactifs", 520, 25);
				g.setColor(Color.red);
				for(int loop = 0 ; loop < nombreCivilisations ; loop++){
					if(eventRandom[loop] == 1)
						g.drawString("CE", 536, loop * 20 + 69);
					if(eventRandom[loop] == 2)
						g.drawString("GC", 536, loop * 20 + 69);
					if(eventRandom[loop] == 3)
						g.drawString("C", 536, loop * 20 + 69);
					if(eventRandom[loop] == 4)
						g.drawString("F", 536, loop * 20 + 69);
					if(eventRandom[loop] == 5)
						g.drawString("CN", 536, loop * 20 + 69);
				}
				//épidémie
				if(presEpidemie == 1){
					g.setColor(Color.red);
					g.drawString("EPIDEMIE", 700, 25);
				}
				g.setColor(Color.black);
				//scnénarios:
				g.drawString("Scénarios actifs :", 1000, 10);
				if(scenario1) g.drawString("1", 1110, 10);
				if(scenario2) g.drawString("2", 1120, 10);
				if(scenario3) g.drawString("3", 1130, 10);
				if(scenario4) g.drawString("4", 1140, 10);
				if(scenario5) g.drawString("5", 1150, 10);
				if(scenario6) g.drawString("6", 1160, 10);
				if(scenario7) g.drawString("7", 1170, 10);
				if(scenario8) g.drawString("8", 1180, 10);
				if(scenario9) g.drawString("9", 1190, 10);
				if(scenario10) g.drawString("10", 1200, 10);
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
				}catch(Exception e){}
			}
		}
		
		
		//DEPART      main:      
		public static void main(String[] args) {
	        //appel de "JeuDesCivilisations" la classe principale:
			new JeuDesCivilisations();
	    }
		//sauvegarde:
		
		
		
		
		//ICI SONT PLACES TOUTES LES ACTIONS DES BOUTONS:
		
		//radiobutton:
		class Changement implements ActionListener{
			public void actionPerformed(ActionEvent e){
				String valeur = (String)((JRadioButton)e.getSource()).getText();
				if(valeur == "très rapide"){
					vitesse = 10;
				}
				if(valeur == "rapide"){
					vitesse = 50;
				}
				if(valeur == "lent"){
					vitesse = 100;
				}
				if(valeur == "très lent"){
					vitesse = 200;
				}
			}
		}
		//bouton pause:
		class PausePlay implements ActionListener{
			public void actionPerformed(ActionEvent e){
				if((String)((JButton) e.getSource()).getText() == "Pause"){
					play.setText("Reprendre");
					animation = false;
				}else{
					play.setText("Pause");
					animation = true;
				}
			}
		}
		//bouton nouveau:
		class Reinitialisation implements ActionListener{
			public void actionPerformed(ActionEvent e){
				animation = false;
				play.setText("Reprendre");
				Generation();
			}
		}
		//bouton save:
		class Sauvegarde implements ActionListener{
			public void actionPerformed(ActionEvent e){
				Sauvegarde("save");
			}
		}
		
		public class Scenarios extends JDialog{
			public Scenarios(JFrame parent, String titre, boolean modal){
				super(parent,titre,modal);
			    this.setResizable(false);
				this.setSize(170, 400);
				this.setLocationRelativeTo(null);
				this.setBackground(new Color(100, 100, 100));
				JPanel panneau = new JPanel();
				this.getContentPane().add(panneau);
				JButton suivant = new JButton("Lancement");
				JRadioButton scenari1 = new JRadioButton("Démographie forte");
				JRadioButton scenari2 = new JRadioButton("Démographie faible");
				JRadioButton scenari3 = new JRadioButton("Science décuplée");
				JRadioButton scenari4 = new JRadioButton("Science réduite");
				JCheckBox scenari5 = new JCheckBox("Guerres boucheries");
				JCheckBox scenari6 = new JCheckBox("Dépression collective");
				JCheckBox scenari7 = new JCheckBox("Crise économique");
				JCheckBox scenari8 = new JCheckBox("Fusions impossibles");
				JCheckBox scenari9 = new JCheckBox("Intolérance des peuples");
				JCheckBox scenari10 = new JCheckBox("Mauvais climat");
				scenari1.addActionListener(new Ecoute());
				scenari2.addActionListener(new Ecoute());
				scenari3.addActionListener(new Ecoute());
				scenari4.addActionListener(new Ecoute());
				scenari5.addActionListener(new Ecoute());
				scenari6.addActionListener(new Ecoute());
				scenari7.addActionListener(new Ecoute());
				scenari8.addActionListener(new Ecoute());
				scenari9.addActionListener(new Ecoute());
				scenari10.addActionListener(new Ecoute());
				suivant.addActionListener(new Suivant());
				ButtonGroup bgDemo = new ButtonGroup(); ButtonGroup bgScience = new ButtonGroup();
				bgDemo.add(scenari1); bgDemo.add(scenari2); bgScience.add(scenari3); bgScience.add(scenari4);
				panneau.add(scenari1);
				panneau.add(scenari2);
				panneau.add(scenari3);
				panneau.add(scenari4);
				panneau.add(scenari5);
				panneau.add(scenari6);
				panneau.add(scenari7);
				panneau.add(scenari8);
				panneau.add(scenari9);
				panneau.add(scenari10);
				panneau.add(suivant);
				this.setVisible(true);
				
				
			}
			class Ecoute implements ActionListener{
				public void actionPerformed(ActionEvent e){
					boolean resultat = ((AbstractButton) e.getSource()).isSelected();
					if(((AbstractButton) e.getSource()).getText() == "Démographie forte"){scenario1 = resultat; scenario2 = !resultat;}
					if(((AbstractButton) e.getSource()).getText() == "Démographie faible"){scenario2 = resultat; scenario1 = !resultat;}
					if(((AbstractButton) e.getSource()).getText() == "Science décuplée"){scenario3 = resultat; scenario4 = !resultat;}
					if(((AbstractButton) e.getSource()).getText() == "Science réduite"){scenario4 = resultat; scenario3 = !resultat;}
					if(((AbstractButton) e.getSource()).getText() == "Guerres boucheries") scenario5 = resultat;
					if(((AbstractButton) e.getSource()).getText() == "Dépression collective") scenario6 = resultat;
					if(((AbstractButton) e.getSource()).getText() == "Crise économique") scenario7 = resultat;
					if(((AbstractButton) e.getSource()).getText() == "Fusions impossibles") scenario8 = resultat;
					if(((AbstractButton) e.getSource()).getText() == "Intolérance des peuples") scenario9 = resultat;
					if(((AbstractButton) e.getSource()).getText() == "Mauvais climat") scenario10 = resultat;
				}
			}
			class Suivant implements ActionListener{
				public void actionPerformed(ActionEvent e){
					setVisible(false);
				}
			}
		}
		
		//sauvegarde:
		public Calendar calendrier;
		public void Sauvegarde(String type){
			File file;
			FileWriter fw;
			this.calendrier = Calendar.getInstance();
			try{
				file = new File(type + this.calendrier.get(Calendar.HOUR_OF_DAY) + "h" + this.calendrier.get(Calendar.MINUTE) + "m" + this.calendrier.get(Calendar.SECOND) + "s" + this.calendrier.get(Calendar.DATE) + "j" + this.calendrier.get(Calendar.MONTH) + "m" + this.calendrier.get(Calendar.YEAR) + ".txt");
				fw = new FileWriter(file);
				String retour = System.getProperty("line.separator");
				//écriture du nombre de civ et date:
		        fw.write(nombreCivilisations + " " + temps + retour);
				//écriture du monde:
				//climat
		        for(int ligne = 0 ; ligne <= 29 ; ligne++){
					for(int colonne = 0 ; colonne <= 49 ; colonne++){
						fw.write(monde[ligne][colonne][0] + " ");
					}fw.write(retour);
		        }
		        //AL
		        fw.write(retour);
		        for(int ligne = 0 ; ligne <= 29 ; ligne++){
					for(int colonne = 0 ; colonne <= 49 ; colonne++){
						fw.write(monde[ligne][colonne][1] + " ");
					}fw.write(retour);
		        }
		        //EEM
		        fw.write(retour);
		        for(int ligne = 0 ; ligne <= 29 ; ligne++){
					for(int colonne = 0 ; colonne <= 49 ; colonne++){
						fw.write(monde[ligne][colonne][2] + " ");
					}fw.write(retour);
		        }
		        //appartenance
		        fw.write(retour);
		        for(int ligne = 0 ; ligne <= 29 ; ligne++){
					for(int colonne = 0 ; colonne <= 49 ; colonne++){
						fw.write(monde[ligne][colonne][3] + " ");
					}fw.write(retour);
		        }
		        fw.write(retour);
				//civilisations:
		        for(int loop = 1 ; loop <= nombreCivilisations ; loop++){
		        	fw.write(loop + " ");
		        	for(int loop2 = 0 ; loop2 <= 21 ; loop2++){
		        		fw.write(civilisations[loop][loop2] + " ");
		        	}
		        	fw.write(retour);
		        }
		        //relations:
		        fw.write(retour);
		        for(int ligne = 0 ; ligne < nombreCivilisations ; ligne++){
					for(int colonne = 0 ; colonne < nombreCivilisations - 1 ; colonne++){
						fw.write(relations[ligne][colonne] + " ");
					}fw.write(retour);
		        }
		        fw.write(retour);
		        //aléatoire:
		        for(int loop = 0 ; loop < nombreCivilisations ; loop++){
		        	fw.write(tempRandom[loop] + " ");
		        }fw.write(retour);
		        for(int loop = 0 ; loop < nombreCivilisations ; loop++){
		        	fw.write(saveNamNam[loop] + " ");
		        }fw.write(retour);
		        for(int loop = 0 ; loop < nombreCivilisations ; loop++){
		        	fw.write(savePol[loop] + " ");
		        }fw.write(retour);
		        for(int loop = 0 ; loop < nombreCivilisations ; loop++){
		        	fw.write(savePop[loop] + " ");
		        }fw.write(retour);
		        for(int loop = 0 ; loop < nombreCivilisations ; loop++){
		        	fw.write(saveEco[loop] + " ");
		        }fw.write(retour);
		        for(int loop = 0 ; loop < nombreCivilisations ; loop++){
		        	fw.write(eventRandom[loop] + " ");
		        }fw.write(retour);
		        //peste:
		        fw.write(retour);
				fw.write(presEpidemie + " ");
				fw.write(powEpidemie + " ");
				fw.write(rangeEpidemie + " ");
				fw.write(retour);
				for(int loop = 0 ; loop < nombreCivilisations ; loop++)
					fw.write(savepop[loop] + " ");
				fw.write(retour);
		        for(int ligne = 0 ; ligne <= 29 ; ligne++){
					for(int colonne = 0 ; colonne <= 49 ; colonne++){
						fw.write(epidemie[ligne][colonne] + " ");
					}fw.write(retour);
		        }
		        //relationsProv:
		        fw.write(retour);
		        for(int ligne = 0 ; ligne < nombreCivilisations ; ligne++){
					for(int colonne = 0 ; colonne < nombreCivilisations - 1 ; colonne++){
						fw.write(relationsProv[ligne][colonne] + " ");
					}fw.write(retour);
		        }
				fw.close();
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
			
		}
		//chargement:
		public void Chargement(String nom){
			File file;
			try{
				file = new File(nom + ".txt");
				Scanner scanner = new Scanner(file);
				//lecture du nombre de civ et date:
		        nombreCivilisations = scanner.nextInt(); temps = scanner.nextLong();
				//lecture du monde:
				//climat
		        for(int ligne = 0 ; ligne <= 29 ; ligne++){
					for(int colonne = 0 ; colonne <= 49 ; colonne++){
						monde[ligne][colonne][0] = scanner.nextInt();
					}
		        }
		        //AL
		        for(int ligne = 0 ; ligne <= 29 ; ligne++){
					for(int colonne = 0 ; colonne <= 49 ; colonne++){
						monde[ligne][colonne][1] = scanner.nextInt();
					}
		        }
		        //EEM
		        for(int ligne = 0 ; ligne <= 29 ; ligne++){
					for(int colonne = 0 ; colonne <= 49 ; colonne++){
						monde[ligne][colonne][2] = scanner.nextInt();
					}
		        }
		        //appartenance
		        for(int ligne = 0 ; ligne <= 29 ; ligne++){
					for(int colonne = 0 ; colonne <= 49 ; colonne++){
						monde[ligne][colonne][3] = scanner.nextInt();
					}
		        }
				//civilisations:
		        civilisations = new int[nombreCivilisations + 1][22];
		        for(int loop = 1 ; loop <= nombreCivilisations ; loop++){
		        	scanner.nextInt();
		        	for(int loop2 = 0 ; loop2 <= 21 ; loop2++){
		        		civilisations[loop][loop2] = scanner.nextInt();
		        	}
		        }
		        //relations:
		        relations = new int[nombreCivilisations][nombreCivilisations - 1];	
		        for(int ligne = 0 ; ligne < nombreCivilisations ; ligne++){
					for(int colonne = 0 ; colonne < nombreCivilisations - 1 ; colonne++){
						relations[ligne][colonne] = scanner.nextInt();
					}
		        }
		        //aléatoire:
		        tempRandom = new int[nombreCivilisations] ;
				saveNamNam = new int[nombreCivilisations];
				savePol = new int[nombreCivilisations];
				savePop = new int[nombreCivilisations];
				saveEco = new int[nombreCivilisations];
				eventRandom = new int[nombreCivilisations];
		        for(int loop = 0 ; loop < nombreCivilisations ; loop++){
		        	tempRandom[loop] = scanner.nextInt();
		        }
		        for(int loop = 0 ; loop < nombreCivilisations ; loop++){
		        	saveNamNam[loop] = scanner.nextInt();
		        }
		        for(int loop = 0 ; loop < nombreCivilisations ; loop++){
		        	savePol[loop] = scanner.nextInt();
		        }
		        for(int loop = 0 ; loop < nombreCivilisations ; loop++){
		        	savePop[loop] = scanner.nextInt();
		        }
		        for(int loop = 0 ; loop < nombreCivilisations ; loop++){
		        	saveEco[loop] = scanner.nextInt();
		        }
		        for(int loop = 0 ; loop < nombreCivilisations ; loop++){
		        	eventRandom[loop] = scanner.nextInt();
		        } 
				presEpidemie = scanner.nextInt();
				powEpidemie = scanner.nextInt();
				rangeEpidemie = scanner.nextInt();
				savepop = new int[nombreCivilisations];
				for(int loop = 0 ; loop < nombreCivilisations ; loop++)
					savepop[loop] = scanner.nextInt();
		        for(int ligne = 0 ; ligne <= 29 ; ligne++){
					for(int colonne = 0 ; colonne <= 49 ; colonne++){
						epidemie[ligne][colonne] = scanner.nextInt();
					}
		        }
		        //relationsProv:
		        relationsProv = new double[nombreCivilisations][nombreCivilisations - 1];
		       for(int ligne = 0 ; ligne < nombreCivilisations ; ligne++){
					for(int colonne = 0 ; colonne < nombreCivilisations - 1 ; colonne++){
						relationsProv[ligne][colonne] = Double.parseDouble(scanner.next());
					}
		       }
		        civilisations[0][18] = 1;
		        civilisations[0][0] = 75; civilisations[0][1] = 75; civilisations[0][2] = 75;
				scanner.close();
			}catch(FileNotFoundException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "ERREUR FICHIER INEXISTANT :(", "ERREUR CHARGEMENT", JOptionPane.ERROR_MESSAGE);
			}catch(NoSuchElementException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "ERREUR FICHIER NON VALIDE :(", "ERREUR CHARGEMENT", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		
}
