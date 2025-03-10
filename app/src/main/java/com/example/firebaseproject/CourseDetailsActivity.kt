package com.example.firebaseproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi

import androidx.compose.material.Card
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.firebaseproject.ui.theme.FirebaseprojectTheme
import com.example.firebaseproject.ui.theme.greenColor

import com.google.firebase.firestore.FirebaseFirestore
import com.example.firebaseproject.UpdateCourse
class CourseDetailsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseprojectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Scaffold(
                        // in scaffold we are specifying the top bar.
                        topBar = {
                            // inside top bar we are
                            // specifying background color.
                            TopAppBar(
                                // along with that we are specifying
                                // title for our top bar.
                                title = {
                                    // in the top bar we are
                                    // specifying tile as a text
                                    Text(
                                        // on below line we are specifying
                                        // text to display in top app bar
                                        text = "Khoa hoc",
                                        // on below line we are specifying
                                        // modifier to fill max width
                                        modifier = Modifier.fillMaxWidth(),
                                        // on below line we are specifying
                                        // text alignment
                                        textAlign = TextAlign.Center,
                                        // on below line we are specifying
                                        // color for our text.
                                        color = Color.White
                                    )
                                })
                        }) { innerPadding ->
                        Text(
                            modifier = Modifier.padding(innerPadding),
                            text = "Cap nhat du lieu."
                        )

                        // on below line creating variable for list of data.
                        var courseList = mutableStateListOf<Course?>()
                        // on below line creating variable for freebase database
                        // and database reference.
                        var db: FirebaseFirestore = FirebaseFirestore.getInstance()

                        // on below line getting data from our database
                        db.collection("Courses").get()
                            .addOnSuccessListener { queryDocumentSnapshots ->
                                // after getting the data we are
                                // calling on success method
                                // and inside this method we are
                                // checking if the received query
                                // snapshot is empty or not.
                                if (!queryDocumentSnapshots.isEmpty) {
                                    // if the snapshot is not empty we are
                                    // hiding our progress bar and adding
                                    // our data in a list.
                                    // loadingPB.setVisibility(View.GONE)
                                    val list = queryDocumentSnapshots.documents
                                    for (d in list) {
                                        // after getting this list we are passing that list
                                        // to our object class.
                                        val c: Course? = d.toObject(Course::class.java)
                                        c?.courseID = d.id
                                        Log.e("TAG", "Course id is : " + c!!.courseID)
                                        // and we will pass this object class
                                        // inside our arraylist which we have
                                        // created for list view.
                                        courseList.add(c)

                                    }
                                } else {
                                    // if the snapshot is empty we are
                                    // displaying a toast message.
                                    Toast.makeText(
                                        this@CourseDetailsActivity,
                                        "No data found in Database",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            // if we don't get any data
                            // or any error we are displaying
                            // a toast message that we donot get any data
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@CourseDetailsActivity,
                                    "Fail to get the data.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        Spacer(modifier = Modifier.size(15.dp))
                        // on below line we are calling method to display UI
                        firebaseUI(LocalContext.current, courseList)

                        // ket thuc

                    }
                }
            }
        }
    }


}

    private fun deleteDataFromFirebase(courseID: String?, context: Context) {

        // getting our instance from Firebase Firestore.
        val db = FirebaseFirestore.getInstance();

        // below line is for getting the collection
        // where we are storing our courses.
        db.collection("Courses").document(courseID.toString()).delete().addOnSuccessListener {
            // displaying toast message when our course is deleted.
            Toast.makeText(context, "Course Deleted successfully..", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, CourseDetailsActivity::class.java))
        }.addOnFailureListener {
            // on below line displaying toast message when
            // we are not able to delete the course
            Toast.makeText(context, "Fail to delete course..", Toast.LENGTH_SHORT).show()
        }

    }


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun firebaseUI(context: Context, courseList: SnapshotStateList<Course?>) {

        // on below line creating a column
        // to display our retrieved list.
        Column(
            // adding modifier for our column
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.White),
            // on below line adding vertical and
            // horizontal alignment for column.
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // on below line we are calling lazy column
            // for displaying listview.
            LazyColumn {
                // on below line we are setting data
                // for each item of our listview.
                itemsIndexed(courseList) { index, item ->
                    // on below line we are creating
                    // a card for our list view item.
                    Card(
                        onClick = {
                            // on below line opening new activity and passing data.
                            val i = Intent(context, UpdateCourse::class.java)
                            i.putExtra("courseName", item?.courseName)
                            i.putExtra("courseDuration", item?.courseDuration)
                            i.putExtra("courseDescription", item?.courseDescription)
                            i.putExtra("courseID", item?.courseID)
                            // inside on click we are opening
                            // new activity to update course details.
                            context.startActivity(i)

                        },
                        // on below line we are adding
                        // padding from our all sides.
                        modifier = Modifier.padding(8.dp),

                        // on below line we are adding
                        // elevation for the card.
                        elevation = 6.dp
                    ) {
                        // on below line we are creating
                        // a row for our list view item.
                        Column(
                            // for our row we are adding modifier
                            // to set padding from all sides.
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            // on below line inside row we are adding spacer
                         //   Spacer(modifier = Modifier.width(20.dp))
                            Spacer(modifier = Modifier.size(15.dp))
                            // on below line we are displaying course name.
                            courseList[index]?.courseName?.let {
                                Text(
                                    // inside the text on below line we are
                                    // setting text as the language name
                                    // from our modal class.
                                    text = it,

                                    // on below line we are adding padding
                                    // for our text from all sides.
                                    modifier = Modifier.padding(4.dp),

                                    // on below line we are adding color for our text
                                    color = greenColor,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(
                                        fontSize = 20.sp, fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            // adding spacer on below line.
                            Spacer(modifier = Modifier.height(5.dp))

                            // on below line displaying text for course duration
                            courseList[index]?.courseDuration?.let {
                                Text(
                                    // inside the text on below line we are
                                    // setting text as the language name
                                    // from our modal class.
                                    text = it,

                                    // on below line we are adding padding
                                    // for our text from all sides.
                                    modifier = Modifier.padding(4.dp),

                                    // on below line we are adding color for our text
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(
                                        fontSize = 15.sp
                                    )
                                )
                            }
                            // adding spacer on below line.
                            Spacer(modifier = Modifier.width(5.dp))

                            // on below line displaying text
                            // for course description
                            courseList[index]?.courseDescription?.let {
                                Text(
                                    // inside the text on below line we are
                                    // setting text as the language name
                                    // from our modal class.
                                    text = it,

                                    // on below line we are adding padding
                                    // for our text from all sides.
                                    modifier = Modifier.padding(4.dp),

                                    // on below line we are
                                    // adding color for our text
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(fontSize = 15.sp)
                                )
                            }

                            // hinh anh o day
                            Spacer(modifier = Modifier.width(5.dp))

                            courseList[index]?.courseDescription?.let {

                                Image(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(104.dp)
                                        .clip(RoundedCornerShape(size = 8.dp)),
                                    painter = rememberAsyncImagePainter(it),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = "hinh anh"
                                )
                            }


                            // xoa du lieu o day
                            Spacer(modifier = Modifier.height(10.dp))

                            // on below line creating button to delete the course.
                            Button(
                                onClick = {

                                    // on below line calling delete course
                                    // method to delete our course.
                                    deleteDataFromFirebase(courseList[index]?.courseID, context)
                                },
                                // on below line we are
                                // adding modifier to our button.
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // on below line we are adding text for our button
                                Text(text = "Delete Course", modifier = Modifier.padding(8.dp))
                            }



                        // ket thuc xoa

                        }
                    }
                }


        }
    }
}



private fun updateDataToFirebase(
    courseID: String?,
    name: String?,
    duration: String?,
    description: String?,
    context: Context
) {
    // inside this method we are passing our updated
    // values inside our object class and later on we
    // will pass our whole object tofirebase Firestore.
    val updatedCourse = Course(courseID, name, duration, description)

    // getting our instance from Firebase Firestore.
    val db = FirebaseFirestore.getInstance();
    db.collection("Courses").document(courseID.toString()).set(updatedCourse)
        .addOnSuccessListener {
            // on below line displaying toast message and
            // opening new activity to view courses.
            Toast.makeText(context, "Course Updated successfully..", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, CourseDetailsActivity::class.java))


        }.addOnFailureListener {
            Toast.makeText(context, "Fail to update course : " + it.message, Toast.LENGTH_SHORT)
                .show()
        }

}


@Composable
fun imageFromURL() {
    // on below line we are creating a column,
    Column(
        // in this column we are adding modifier
        // to fill max size, mz height and max width
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth()
            // on below line we are adding
            // padding from all sides.
            .padding(10.dp),
        // on below line we are adding vertical
        // and horizontal arrangement.
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // on below line we are adding image for our image view.
        Image(
            // on below line we are adding the image url
            // from which we will  be loading our image.
            painter = rememberAsyncImagePainter("https://www.elle.vn/wp-content/uploads/2016/06/29/dien-vien-viet-nam-trien-vong-1.jpg"),

            // on below line we are adding content
            // description for our image.
            contentDescription = "gfg image",

            // on below line we are adding modifier for our
            // image as wrap content for height and width.
            modifier = Modifier
                .wrapContentSize()
                .wrapContentHeight()
                .wrapContentWidth()
        )
    }
}
