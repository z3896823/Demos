package org.zyb.recyclerviewtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 *
 * RecyclerView使用：
 * 1.导入v7包
 * 2.编写Adapter：
 * 2.1.在适配器的构造方法中传入数据包
 * 2.2.在onCreateViewHolder()中inflate custom view of item，然后将这个对象传入ViewHolder的构造方法中，
 *     并生成一个ViewHolder对象，最后将这个对象返回。
 * 2.3.添加内部类ViewHolder继承自RecyclerView.ViewHolder，在构造方法中获得布局的实例，
 *     然后根据这个布局去findViewById找到控件，并使这些控件的对象成为ViewHolder的成员变量
 * 2.4.在onBindViewHolder()中将会得到onCreateViewHolder()返回的ViewHolder对象，对这个对象的成员变量
 *     即布局中的控件，进行数据的填充工作。
 * 3.ListView的管理依靠自身，所以只能有一种布局，而RecyclerView的管理交给LayoutManager来做
 *   所以在使用RecyclerView的时候，除了要setAdapter还要setLayoutManager，通过不同的LayoutManager，
 *   即可实现同一个RecyclerView呈现不同的效果。
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Fruit> fruitList = initFruit() ;

        RecyclerView rv_recyclerView = (RecyclerView) findViewById(R.id.id_rv_list);

        //region横向排列
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //endregion

        //瀑布流
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);

        rv_recyclerView.setLayoutManager(layoutManager);

        FruitAdapter fruitAdapter = new FruitAdapter(fruitList);

        rv_recyclerView.setAdapter(fruitAdapter);

    }

    private List<Fruit> initFruit(){

        List<Fruit> fruitList = new ArrayList<>();

        Fruit apple = new Fruit(getRandomLengthName("Apple"),R.drawable.apple_pic);
        Fruit banana = new Fruit(getRandomLengthName("Banana"),R.drawable.banana_pic);
        Fruit cherry = new Fruit(getRandomLengthName("Cherry"),R.drawable.cherry_pic);
        Fruit grape = new Fruit(getRandomLengthName("Grape"),R.drawable.grape_pic);
        Fruit mango = new Fruit("Mango",R.drawable.mango_pic);
        Fruit orange = new Fruit("Orange",R.drawable.orange_pic);
        Fruit pear = new Fruit("Pear",R.drawable.pear_pic);
        Fruit pineapple = new Fruit("Pineapple",R.drawable.pineapple_pic);
        Fruit strawberry = new Fruit("Strawberry",R.drawable.strawberry_pic);
        Fruit watermelon = new Fruit("Watermelon",R.drawable.watermelon_pic);

        fruitList.add(apple);
        fruitList.add(banana);
        fruitList.add(cherry);
        fruitList.add(grape);
        fruitList.add(mango);
        fruitList.add(orange);
        fruitList.add(pear);
        fruitList.add(pineapple);
        fruitList.add(strawberry);
        fruitList.add(watermelon);

        return fruitList;
    }

    public String getRandomLengthName(String name){
        Random random = new Random();
        int length = random.nextInt(20) + 1;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i<length;i++){
            builder.append(name);
        }
        return builder.toString();
    }
}
