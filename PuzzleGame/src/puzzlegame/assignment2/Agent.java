package puzzlegame.assignment2;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.SwingUtilities;

import org.omg.CORBA.SystemException;

import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Color;

class Agent {
	Stack path = new Stack<Block>();
	int accum = 0;
	Block current;
	int destinations[][] = new int[80][80];
	float heuristic = 5;
	boolean heuristicFound = false;
	
	void drawPlan(Graphics g, Model m) {
		g.setColor(Color.red);
		int i = m.getDestNum();
		State[] dest = m.getDestinations();
		ArrayList<State> visited = m.getVisited();
		//System.out.println("ArrayList size:" + visited.size());
		for(int in = 0; in < visited.size(); in++){
			g.drawOval(visited.get(in).x,visited.get(in).y,5,5);
		}
			
		g.setColor(Color.black);
		for(int x = 0; x < i-1; x++){
			g.drawLine(dest[x].x,dest[x].y, dest[x+1].x,dest[x+1].y);
			
		}
		
	}

	void update(Model m)
	{
		Controller c = m.getController();
		accum++;
		if(accum == 7){
			
			if(!path.isEmpty()){
				current = (Block) path.pop();
			//	current.print();
				m.setDestination(current.x, current.y);
			}
			accum = 0;
		}

		while(true)
		{
			MouseEvent e = c.nextMouseEvent();
			
			if(e == null)
				break;
			else if(SwingUtilities.isLeftMouseButton(e)){
				
				m.emptyDestinations();
				Block startState = new Block(m.getX(),m.getY(),(float) 0.0,null);
				UFS ufs = new UFS(false,(float)0.0);
				int x = (e.getX()/10)*10;
				int y = (e.getY()/10)*10;
				Block goal = new Block(x,y,(float) 0.0,null);
				//Block goal = new Block(m.getX()+1020,m.getY()+290,(float) 0.0,null);
				//Do UFS with no A*
				path = ufs.uniform_cost_search(m, startState, goal);
			}
			else if(SwingUtilities.isRightMouseButton(e)){
				System.out.println("Doing A* Search");
				if(!heuristicFound)
				calculateHeuristic(m);
				
				m.emptyDestinations();
				Block startState = new Block(m.getX(),m.getY(),(float) 0.0,null);
				UFS ufs = new UFS(true, heuristic);
				System.out.println("Clicked: " + e.getX() + "," + e.getY());
				int x = (e.getX()/10)*10;
				int y = (e.getY()/10)*10;
				System.out.println("New values: " + x + "," + y);
				Block goal = new Block(x,y,(float) 0.0,null);
				
				//Do UFS with A*
				path = ufs.uniform_cost_search(m, startState, goal);
			}
			
		}
	}

	private void calculateHeuristic(Model m) {
		
		float temp;
		for(int x = 0; x < 1200; x+=10)
			for(int y =0; y < 600; y+=10){
				temp = 10/(m.getTravelSpeed(x,y));
				if(temp < heuristic)
					heuristic = temp;
			}
		heuristicFound = true;
		System.out.println("Using Heuristic value of: " + heuristic);
	}

	public static void main(String[] args) throws Exception
	{
		Controller.playGame();
	}
}

class Block {
	  public float cost;
	  Block parent;
	  float x;
	  float y;
	  
	  Block(float x, float y, float cost, Block par) {
		  this.cost = cost;
		  this.parent = par;
		  this.x = x;
		  this.y = y;
	  }
	  
	  void print(){
		  System.out.println("(" + x + "," + y + ") cost : " + cost);
	  }
	}

class BlockComparator implements Comparator<Block>
{
	public int compare(Block a, Block b)
	{
		
		  Float x1 = a.x;
	        Float x2 = b.x;
	        int floatCompare1 = x1.compareTo(x2);

	        if (floatCompare1 != 0) {
	            return floatCompare1;
	        } else {
	            Float y1 = a.y;
	            Float y2 = b.y;
	            return y1.compareTo(y2);
	        }
	    }
		
		
	}
class CostComparator implements Comparator<Block>
{
	public int compare(Block a, Block b)
	{
			if(a.cost > b.cost)
				return 1;
			else if(a.cost < b.cost)
					return -1;
		return 0;
	}
}  

class UFS {
	BlockComparator comp;
	CostComparator costComp;
	PriorityQueue<Block> frontier;
	TreeSet<Block> beenThere;
	Stack<Block> path;
	boolean aStar;
	float heuristic;
	
	public UFS(boolean a, float h){
		this.aStar = a;
		this.heuristic = h;
	}
	
	  public Stack<Block> uniform_cost_search(Model m, Block startState, Block goal) {
		boolean found = false;
		comp = new BlockComparator();
		costComp = new CostComparator();
		frontier = new PriorityQueue<Block>(costComp);
		beenThere = new TreeSet<Block>(comp);
		path = new Stack<Block>();
	    frontier.add(startState);
	    beenThere.add(startState);
	    
	    while(frontier.size() > 0) {
	      Block s = (Block) frontier.remove(); // get lowest-cost state
	      
	      //s.print();
	      if(s.x == goal.x && s.y == goal.y){
	    	  System.out.println("Found dest");
	    	  goal.parent = s;
	    	  found = true;
	    	  break;
	      }
	      //
	      MoveUpRight(m,s); //x+10, y-10;
	      MoveRight(m,s); //x+10
	      MoveDownRight(m,s); //x+10, y+10
	      MoveDown(m,s); //y+10
	      MoveDownLeft(m,s);//x-10, y+10
	      MoveLeft(m,s); //x-10
	      MoveUpLeft(m,s);//x-10, y-10
	      MoveUp(m,s); //y-10
	     
	    } 
	    
	    if(!found){
	    	System.out.println("Can't find route");
	    	return new Stack();
	    }
	    
	    System.out.println("TreeeSet size: " + beenThere.size());
	    
	    Block current = goal;
	    while(current!=null){
	    	//current.print();
	    	path.add(current);
	    	current = current.parent;
	    	
	    }
	    
	    return path;
	    //throw new RuntimeException("There is no path to the goal");
	  }

	private void MoveUpLeft(Model m, Block root) {
		float x = (float) (root.x-10);
		float y = (float) (root.y-10); //MAY BE TOO SLOW
			
		if(x > 0 && y > 0){
			float cost = (float)(10/(m.getTravelSpeed(x,y))*Math.sqrt(2))+ root.cost; //Cost is speed associated with the terrain square AND distance you will travel at that speed
			
			if(aStar)
				cost = cost - heuristic;
			
			Block child = new Block(x,y,cost,root);
			Block oldChild;
		
			if(beenThere.contains(child)){ //If the new block is already in the set, then we need to check cost
				oldChild = beenThere.floor(child); //find the block with the same x,y
				//System.out.println("Down. Found : " + x + "," + y + " already in beenThere. oldChildCost: " + oldChild.x + "," + oldChild.y + " newChild: " + child.x + "," + child.y);
				if(cost < oldChild.cost) { //If the root cost + new cost is less than old cost, then update new cost and make 
			        oldChild.cost =  cost; //new parent
			        oldChild.parent = root;
			      }	
			}
			else {	//If its not in the set, add it to the set, dont care about cost
				//System.out.println("Down. Adding: x : " + child.x + " y : " + child.y );
				frontier.add(child);
				beenThere.add(child);
				m.setVisited((int)child.x,(int)child.y);
			}
		}
	}

	private void MoveDownLeft(Model m, Block root) {
		float x = (float) (root.x-10);
		float y = (float) (root.y+10); //MAY BE TOO SLOW
		
		if(x > 0 && y < 599){
			float cost = (float)(10/(m.getTravelSpeed(x,y))*Math.sqrt(2))+ root.cost;//Cost is speed associated with the terrain square AND distance you will travel at that speed
			
			if(aStar)
				cost = cost - heuristic;
			
			Block child = new Block(x,y,cost,root);
			Block oldChild;
		
			if(beenThere.contains(child)){ //If the new block is already in the set, then we need to check cost
				oldChild = beenThere.floor(child); //find the block with the same x,y
				//System.out.println("Down. Found : " + x + "," + y + " already in beenThere. oldChildCost: " + oldChild.x + "," + oldChild.y + " newChild: " + child.x + "," + child.y);
				if(cost < oldChild.cost) { //If the root cost + new cost is less than old cost, then update new cost and make 
			        oldChild.cost =  cost; //new parent
			        oldChild.parent = root;
			      }	
			}
			else {	//If its not in the set, add it to the set, dont care about cost
				//System.out.println("Down. Adding: x : " + child.x + " y : " + child.y );
				frontier.add(child);
				beenThere.add(child);
				m.setVisited((int)child.x,(int)child.y);
			}
		}
		
	}

	private void MoveDownRight(Model m, Block root) {
		float x = (float) (root.x+10);
		float y = (float) (root.y+10); //MAY BE TOO SLOW
		
		if(x < 1199 && y < 599){
			float cost = (float)(10/(m.getTravelSpeed(x,y))*Math.sqrt(2))+ root.cost;//Cost is speed associated with the terrain square AND distance you will travel at that speed
			
			if(aStar)
				cost = cost - heuristic;
			
			Block child = new Block(x,y,cost,root);
			Block oldChild;
		
			if(beenThere.contains(child)){ //If the new block is already in the set, then we need to check cost
				oldChild = beenThere.floor(child); //find the block with the same x,y
				//System.out.println("Down. Found : " + x + "," + y + " already in beenThere. oldChildCost: " + oldChild.x + "," + oldChild.y + " newChild: " + child.x + "," + child.y);
				if(cost < oldChild.cost) { //If the root cost + new cost is less than old cost, then update new cost and make 
			        oldChild.cost =  cost; //new parent
			        oldChild.parent = root;
			      }	
			}
			else {	//If its not in the set, add it to the set, dont care about cost
				//System.out.println("Down. Adding: x : " + child.x + " y : " + child.y );
				frontier.add(child);
				beenThere.add(child);
				m.setVisited((int)child.x,(int)child.y);
			}	
		}
		
	}

	private void MoveUpRight(Model m, Block root) {
		float x = (float) (root.x+10);
		float y = (float) (root.y-10); //MAY BE TOO SLOW
		
		if(x<1100 && y > 0){
			float cost = (float)(10/(m.getTravelSpeed(x,y))*Math.sqrt(2))+ root.cost;//Cost is speed associated with the terrain square AND distance you will travel at that speed
			
			if(aStar)
				cost = cost - heuristic;
			
			Block child = new Block(x,y,cost,root);
			Block oldChild;
		
			if(beenThere.contains(child)){ //If the new block is already in the set, then we need to check cost
				oldChild = beenThere.floor(child); //find the block with the same x,y
				//System.out.println("Down. Found : " + x + "," + y + " already in beenThere. oldChildCost: " + oldChild.x + "," + oldChild.y + " newChild: " + child.x + "," + child.y);
				if(cost < oldChild.cost) { //If the root cost + new cost is less than old cost, then update new cost and make 
			        oldChild.cost =  cost; //new parent
			        oldChild.parent = root;
			      }	
			}
			else {	//If its not in the set, add it to the set, dont care about cost
				//System.out.println("Down. Adding: x : " + child.x + " y : " + child.y );
				frontier.add(child);
				beenThere.add(child);
				m.setVisited((int)child.x,(int)child.y);
			}
	}
		
	}

	private void MoveUp(Model m, Block root) {
		
		float x = (float) (root.x);
		float y = (float) (root.y-10); //MAY BE TOO SLOW
		
		if(y > 0){
			float cost = (float)(10/(m.getTravelSpeed(x,y)))+ root.cost; //Cost is speed associated with the terrain square AND distance you will travel at that speed
			if(aStar)
				cost = cost - heuristic;
			
			Block child = new Block(x,y,cost,root);
			Block oldChild;
		
			if(beenThere.contains(child)){ //If the new block is already in the set, then we need to check cost
				oldChild = beenThere.floor(child); //find the block with the same x,y
				//System.out.println("Down. Found : " + x + "," + y + " already in beenThere. oldChildCost: " + oldChild.x + "," + oldChild.y + " newChild: " + child.x + "," + child.y);
				if(cost < oldChild.cost) { //If the root cost + new cost is less than old cost, then update new cost and make 
			        oldChild.cost =  cost; //new parent
			        oldChild.parent = root;
			      }	
			}
			else {	//If its not in the set, add it to the set, dont care about cost
				//System.out.println("Down. Adding: x : " + child.x + " y : " + child.y );
				frontier.add(child);
				beenThere.add(child);
				m.setVisited((int)child.x,(int)child.y);
			}
		}
	}

	private void MoveLeft(Model m, Block root) {
		
		float x = (float) (root.x-10);
		float y = (float) (root.y); //MAY BE TOO SLOW
		
		if(x > 0){
			float cost = (float)(10/(m.getTravelSpeed(x,y)))+ root.cost; //Cost is speed associated with the terrain square AND distance you will travel at that speed
			if(aStar)
				cost = cost - heuristic;
			
			Block child = new Block(x,y,cost,root);
			Block oldChild;
		
			if(beenThere.contains(child)){ //If the new block is already in the set, then we need to check cost
				oldChild = beenThere.floor(child); //find the block with the same x,y
				//System.out.println("Down. Found : " + x + "," + y + " already in beenThere. oldChildCost: " + oldChild.x + "," + oldChild.y + " newChild: " + child.x + "," + child.y);
				if(cost < oldChild.cost) { //If the root cost + new cost is less than old cost, then update new cost and make 
			        oldChild.cost =  cost; //new parent
			        oldChild.parent = root;
			      }	
			}
			else {	//If its not in the set, add it to the set, dont care about cost
				//System.out.println("Down. Adding: x : " + child.x + " y : " + child.y );
				frontier.add(child);
				beenThere.add(child);
				m.setVisited((int)child.x,(int)child.y);
			}
		}
	
	}

	private void MoveDown(Model m, Block root) {
		//System.out.println("Moving down.");
		float x = (float) (root.x);
		float y = (float) (root.y+10); //MAY BE TOO SLOW
		
		if(y < 599){
			float cost = (float)(10/(m.getTravelSpeed(x,y)))+ root.cost; //Cost is speed associated with the terrain square AND distance you will travel at that speed
			if(aStar)
				cost = cost - heuristic;
			
			Block child = new Block(x,y,cost,root);
			Block oldChild;
		
			if(beenThere.contains(child)){ //If the new block is already in the set, then we need to check cost
				oldChild = beenThere.floor(child); //find the block with the same x,y
				//System.out.println("Down. Found : " + x + "," + y + " already in beenThere. oldChildCost: " + oldChild.x + "," + oldChild.y + " newChild: " + child.x + "," + child.y);
				if(cost < oldChild.cost) { //If the root cost + new cost is less than old cost, then update new cost and make 
			        oldChild.cost =  cost; //new parent
			        oldChild.parent = root;
			      }	
			}
			else {	//If its not in the set, add it to the set, dont care about cost
				//System.out.println("Down. Adding: x : " + child.x + " y : " + child.y );
				frontier.add(child);
				beenThere.add(child);
				m.setVisited((int)child.x,(int)child.y);
			}
		}

	}

	private void MoveRight(Model m, Block root) {
		
		//System.out.println("Moving down.");
		float x = (float) (root.x+10);
		float y = (float) (root.y); //MAY BE TOO SLOW
		
		if(x < 1199){
			float cost = (float)(10/(m.getTravelSpeed(x,y)))+ root.cost;//Cost is speed associated with the terrain square AND distance you will travel at that speed
			if(aStar)
				cost = cost - heuristic;
			
			Block child = new Block(x,y,cost,root);
			Block oldChild;
			
			//System.out.println("beenthere contains child? :" + beenThere.contains(child) + " x : " + child.x + " y : " + child.y );
			
			if(beenThere.contains(child)){ //If the new block is already in the set, then we need to check cost
				oldChild = beenThere.floor(child); //find the block with the same x,y
				//System.out.println("Right. Found : " + x + "," + y + " already in beenThere. oldChildCost: " + oldChild.x + "," + oldChild.y + " newChild: " + child.x + "," + child.y);
				if(cost < oldChild.cost) { //If the root cost + new cost is less than old cost, then update new cost and make 
			        oldChild.cost =  cost; //new parent
			        oldChild.parent = root;
			      }	
			}
			else {	//If its not in the set, add it to the set, dont care about cost
				//System.out.println("Right. Adding: x : " + child.x + " y : " + child.y );
				frontier.add(child);
				beenThere.add(child);
				m.setVisited((int)child.x,(int)child.y);
			}
		}

      }//End moveRight
}
		
