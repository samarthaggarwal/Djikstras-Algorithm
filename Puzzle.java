import java.util.Scanner;
import java.util.*;
import java.io.*;
import java.lang.*;

class vertex{
	String state;//sequence of 12345678G
	int cost=Integer.MAX_VALUE - 3000;//cost to reach source
	int d=Integer.MAX_VALUE - 3000;//distance to source
	vertex prev=null;
	boolean cloud=false;//whether it is present in the cloud or not
	int index;//index at which this vertex is placed in vector representing graph
	int hid=-1;

	public vertex(String s,int i){
		state = s;
		index=i;
	}
}

class minheap{
	Vector<vertex> v;
	int l;

	public minheap(/*Vector<node> vect,*/vertex start){
		v = new Vector<vertex>();//9! = 362880
		v.setSize(362885);//reduce to 181440
		l = 1;//first empty position, we will keep index 0 empty
		//System.out.println(v.size());

		v.set(l,start);
		start.hid=l;
		l++;

		/*for(int i=0;i<362880;i++){
			if(start==vect.get(i).ver)
				continue;
			v.set(l,vect.get(i).ver);
			v.get(l).hid=l;
			l++;
		}*/
	}

	public void insert(vertex ver){
		v.set(l,ver);
		ver.hid=l;
		percolate(l);
		l++;
	}

	public vertex deletemin(){
		if(l==1){
			System.out.println("called delete on empty heap");
		}

		//should be called only if l>1
		vertex ver = v.get(1);
		ver.hid=-1;
		v.set(1,v.get(--l));
		v.get(1).hid=1;
		percolate(1);

		return ver;
	}

	public void percolate(int pos){
		vertex ver = v.get(pos);

		if(pos>1 && (ver.cost<v.get(pos/2).cost || (ver.cost==v.get(pos/2).cost && ver.d<v.get(pos/2).d) ) ) {//percolate up
			v.set(pos,v.get(pos/2));
			v.set(pos/2,ver);
			v.get(pos).hid=pos;
			v.get(pos/2).hid=pos/2;

			percolate(pos/2);
		}
		else if(pos<l/2){//percolate down
			int i = 2*pos;
			if(i+1<l && ( v.get(i).cost>v.get(i+1).cost || (v.get(i).cost==v.get(i+1).cost && v.get(i).d>v.get(i+1).d) ) )
				i++;

			if(ver.cost>v.get(i).cost || (ver.cost==v.get(i).cost  &&  ver.d>v.get(i).d) ){
				v.set(pos,v.get(i));
				v.set(i,ver);
				v.get(pos).hid=pos;
				v.get(i).hid=i;
				percolate(i);
			}
		}
	}

	public void perdown(int pos){
		vertex ver = v.get(pos);

		if(pos<=l/2){//percolate down
			int i = 2*pos;
			if(i+1<l && ( v.get(i).cost>v.get(i+1).cost || (v.get(i).cost==v.get(i+1).cost && v.get(i).d>v.get(i+1).d) ) )
				i++;

			if(ver.cost>v.get(i).cost || (ver.cost==v.get(i).cost  &&  ver.d>v.get(i).d) ){
				v.set(pos,v.get(i));
				v.set(i,ver);
				v.get(pos).hid=pos;
				v.get(i).hid=i;
				perdown(i);
			}
		}
	}

	public void update(vertex ver){//takes a vertix and updates its position in the minheap
		//int pos;
		//pos = v.indexOf(ver);//STORE THIS VALUE IN VERTEX TO SAVE SEARCH TIME
		if(ver.hid==-1){
			v.set(l,ver);
			ver.hid=l;
			percolate(l);
			l++;
		}
		else
			percolate(ver.hid);

		return;
	}
}

class node{
	vertex ver;
	node next;

	public node(vertex v, node n){
		ver=v;
		next=n;
	}
}

public class Puzzle{

	static int pos=0;

	public static String swap(String s,int i, int j){
		char c;
		char[] arr = s.toCharArray();
		c=arr[i];
		arr[i]=arr[j];
		arr[j]=c;

		return new String(arr);
	}

	public static void permute(String s,int l, int r,Vector<node> graph,HashMap<String,vertex> map){
		if(l==r){
			//try{
			//	bw.write(s+"\n");
			//}
			//catch(IOException e){}
			vertex v = new vertex(s,pos);
			node n = new node(v,null);
			graph.set(pos,n);
			map.put(s,v);
			pos++;
		}
		else{
			for(int i=l;i<=r;i++){
				s=swap(s,l,i);
				permute(s,l+1,r,graph,map);
				s=swap(s,l,i);
			}
		}
	}

	public static String path(vertex v){
		String s = "";
		if(v.prev==null)
			return s;

		vertex temp = v.prev;
		int l = temp.state.indexOf('G');
		int f = v.state.indexOf('G');

		s+=path(temp);
		s+=v.state.charAt(l);
		if(l==f+1 && f%3 <2)
			s+="R ";
		else if(l==f-1 && f%3 >0)
			s+="L ";
		else if(f<6 && l==f+3)
			s+="D ";
		else if(f>2 && l==f-3)
			s+="U ";

		return s;
	}

	public static void main(String args[]){
		//System.out.println(Integer.MAX_VALUE);
		try{
			// FileInputStream fstream = new FileInputStream(args[0]);
			// Scanner s = new Scanner(fstream);
			File f = new File(args[0]);
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			File f1 = new File(args[1]);
			FileWriter fr1 = new FileWriter(f1);
			BufferedWriter bw = new BufferedWriter(fr1);

			int t = Integer.parseInt(br.readLine());
			vertex start,goal,temp,temp2,exp;
			int[] movecost = new int[8];
			String s1,s2;
			int x,y;
			node temp_n;
			HashMap<String,vertex> map = new HashMap<String,vertex>(362885);
			Vector<node> graph = new Vector<node>();

			if(t>0){//generate graph
				graph.setSize(362885);//5 extra indices

				permute("12345678G",0,8,graph,map);

				//bw.write(pos + "\n");
				for(int i=0;i<362880;i++){
					temp_n = graph.get(i);
					s1=temp_n.ver.state;
					x=s1.indexOf('G');
					if(x<6){
						s2 = swap(s1,x,x+3);
						temp = map.get(s2);
						temp_n.next = new node(temp,temp_n.next);
						//e++;
					}
					if(x>2){
						s2 = swap(s1,x,x-3);
						temp = map.get(s2);
						temp_n.next = new node(temp,temp_n.next);
						//e++;
					}
					if(x%3 < 2){
						s2 = swap(s1,x,x+1);
						temp = map.get(s2);
						temp_n.next = new node(temp,temp_n.next);
						//e++;
					}
					if(x%3 >0){
						s2 = swap(s1,x,x-1);
						temp = map.get(s2);
						temp_n.next = new node(temp,temp_n.next);
						//e++;
					}

					//pos--;
				}
				//bw.write(e + "\n");

				/*for(int j=0;j<362880;j++){//printing the adgacency list
					temp_n = graph.get(j);
					bw.write(temp_n.ver.state + "\t-\t");
					temp_n = temp_n.next;
					while(temp_n!=null){
						bw.write(temp_n.ver.state + "\t");
						temp_n = temp_n.next;
					}
					bw.write("\n");
				}*/
			}
			for(int i=0;i<t;i++){
				//System.out.println(i);
				s1=br.readLine();
				s2=s1.substring(s1.indexOf(' ')+1);
				s1=s1.substring(0,s1.indexOf(' '));

				while(s1.charAt(s1.length()-1)==' ')
					s1 = s1.substring(0,s1.length()-1);
				while(s2.charAt(s2.length()-1)==' ')
					s2 = s2.substring(0,s2.length()-1);
				
				/*
				exp = map.get("12346857G");
				if(exp==null)
					System.out.println("not present");
				else
					System.out.println("present" + i);
				*/

				start = map.get(s1);
				goal = map.get(s2);
				//System.out.println("start =" + s1 + "=\tgoal =" + s2 + "=\n");
				//System.out.println("start string" + start.state + "");
				//System.out.println(map.get(s2));
				//System.out.println("goal string" + goal.state + "\n");

				s1=br.readLine();
				//bw.write("\n\nprinting s1 " + s1 + "\n");
				//s1=br.readLine();
				for(int j=0;j<7;j++){
					//System.out.println(s1);
					s2=s1.substring(0,s1.indexOf(' '));
					s1=s1.substring(s1.indexOf(' ') + 1);
					movecost[j]=Integer.parseInt(s2);
				}
				if( s1.length()>0 && s1.charAt(s1.length()-1)==' ')
					s1 = s1.substring(0,s1.length()-1);
				movecost[7]=Integer.parseInt(s1);

				/*for(int j=0;j<8;j++)
					bw.write(movecost[j] + " ");
				bw.write("\n");
				*/


				for(int j=0;j<362880;j++){
					temp = graph.get(j).ver;
					temp.prev=null;
					temp.cost=Integer.MAX_VALUE - 3000;
					temp.d=Integer.MAX_VALUE - 3000;
					temp.cloud=false;
					temp.hid=-1;
				}

				start.cost=0;
				start.d=0;
				//start.cloud=true;
				//System.out.println("after init start cost" + start.state + start.cost + "\n");
				//System.out.println("after init goal cost" + goal.state + goal.cost + "\n");

				minheap mh = new minheap(start);

				// temp = mh.deletemin();
				// if(temp == start)
				// 	System.out.println("yes\n");
				// else
				// 	System.out.println("no\n");

				// mh.insert(temp);

				do{
					temp = mh.deletemin();
					if(temp.cost==Integer.MAX_VALUE - 3000)
						break;
					
					temp.cloud=true;
					temp_n = graph.get(temp.index).next;
					x=temp.state.indexOf('G');
					while(temp_n!=null){
						temp2=temp_n.ver;
						if(temp2.cloud==false){
							y=movecost[temp2.state.charAt(x)-'1'];
							if(temp2.cost> temp.cost + y){
								temp2.cost = temp.cost + y;
								temp2.d = temp.d + 1;
								temp2.prev = temp;
								mh.update(temp2);
							}
							else if( temp2.cost==temp.cost + y && temp2.d > temp.d + 1){
								temp2.d = temp.d + 1;
								temp2.prev = temp;
								mh.update(temp2);
							}
						}
						temp_n=temp_n.next;
					}
				}while(mh.l>1 && temp!=goal);

				//System.out.println("mh.l = " + mh.l);

				if(goal.cost==Integer.MAX_VALUE - 3000){
					bw.write("-1 -1\n\n");
					continue;
				}
				else{
					bw.write(goal.d + " " + goal.cost + "\n");
					s1 = path(goal);
					//if( s1.length()>0 && s1.charAt(s1.length()-1)==' ')
					//	s1 = s1.substring(0,s1.length()-1);

					bw.write(s1 + "\n");
				}
				
			}
			

			br.close();
			bw.close();
		}
		catch(IOException e){}
	}
}

			//heap checker - add in main
			// node v1;
			// Vector<node> vect = new Vector<node>();
			// for(int i=0;i<25;i++){
			// 	v1 = new node(new vertex(String.valueOf(i),i) , null);
			// 	v1.ver.cost = (int)(Math.random()*1000);
			// 	vect.add(v1);
			// }
			// minheap m = new minheap(vect);
			// while (m.l>1){
			// 	// for(int i=1;i<m.l;i++)
			// 	// 	bw.write(m.v.get(i).cost + " ");
			// 	// bw.write("\n");
			// 	bw.write(m.deletemin().cost + " ");
			// }