package puzzlegame.assignment4;

import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

class Node {
    List<Node> children = new ArrayList<Node>();
    Node parent = null;
    char[] data = null;

    public Node(char[] data) {
        this.data = data;
    }

    public Node(char[] data, Node parent) {
        this.data = data;
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setParent(Node parent) {
        parent.addChild(this);
        this.parent = parent;
    }

    public void addChild(char[] data) {
        Node child = new Node(data);
        child.setParent(this);
        this.children.add(child);
    }

    public void addChild(Node child) {
     //   child.setParent(this);
        this.children.add(child);
    }

 

    public boolean isRoot() {
        return (this.parent == null);
    }

    public boolean isLeaf() {
        if(this.children.size() == 0) 
            return true;
        else 
            return false;
    }

    public void removeParent() {
        this.parent = null;
    }
}



public class TicTacToe {
	Node root;
	Queue queue;
	public TicTacToe(){
		char[] board = {'1','2','3','4','5','6','7','8','9'};
		root = new Node(board);
		queue = new LinkedList<Node>();
		queue.add(root);
		calculateBoardStates(root,'X');
		//drawBoard(board);
	}
	
	public void drawBoard(char[] board){
		
		System.out.println(" " + board[0] + " | " + board[1] + " | " + board[2] + " ");
		System.out.println("---+---+---");
		System.out.println(" " + board[3] + " | " + board[4] + " | " + board[5] + " ");
		System.out.println("---+---+---");
		System.out.println(" " + board[6] + " | " + board[7] + " | " + board[8] + " ");
		
	}
	
	public void calculateBoardStates(Node root, char player){
		
		while(!queue.isEmpty()){
			Node current = (Node) queue.remove();
			addState(1,player,current);
			addState(2,player, current);
			addState(3,player, current);
			addState(4,player, current);
			addState(5,player, current);
			addState(6,player, current);
			addState(7,player, current);
			addState(8,player, current);
			addState(9,player, current);
			
			if(player=='X')
				player = 'O';
			else if(player=='O')
				player = 'X';
		}
		
		//for(int x = 0; x < 9; x++){
		Node curr = root;
		int depth = 0;
		while(curr.children.size()!=0){	
			drawBoard(curr.children.get(0).data);
			curr = curr.children.get(0);
			depth++;
			System.out.println("\n " + depth + "\n");
			
		}
		
	}
	
	public void addState(int x, char player, Node root){
		
		char[] boardCopy = root.data.clone();
		char c = root.data[x-1];
		
		//System.out.println("Adding: " + x  + " player: " + player);
		
		if(!gameCompleted(boardCopy))
			if(c != 'X' && c!='O'){
				boardCopy[x-1] = player;
				Node node = new Node(boardCopy, root);
				queue.add(node);
				root.addChild(node);
			}
	}
	
	public boolean gameCompleted(char[] board){
		for(int x = 0; x < 9; x++)
			if(board[x] != 'X' || board[x] !='O')
				return false;
		
		return true;
	}
	
	public static void main(String[] args){
		System.out.println("Playing tictactoe");
		TicTacToe t = new TicTacToe();
		
	}
}
