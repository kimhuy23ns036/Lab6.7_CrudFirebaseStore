package com.example.firebaseproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebaseproject.ui.theme.FirebaseprojectTheme
import com.example.firebaseproject.ui.theme.greenColor
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val db = FirebaseFirestore.getInstance()

    fun addCourse(
        courseName: String,
        courseDuration: String,
        courseDescription: String,
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val courseId = UUID.randomUUID().toString()
                val course = Course(
                    courseID = courseId,
                    courseName = courseName,
                    courseDuration = courseDuration,
                    courseDescription = courseDescription
                )

                db.collection("Courses").document(courseId).set(course)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Course added successfully", Toast.LENGTH_SHORT).show()
                        _isLoading.value = false
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        _error.value = e.message
                        Toast.makeText(context, "Failed to add course: ${e.message}", Toast.LENGTH_SHORT).show()
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                _isLoading.value = false
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseprojectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Khoa Hoc",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = greenColor
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                CourseInputForm(
                    viewModel = viewModel,
                    context = context,
                    onViewCourses = {
                        context.startActivity(Intent(context, CourseDetailsActivity::class.java))
                    }
                )
            }

            error?.let {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseInputForm(
    viewModel: MainViewModel,
    context: Context,
    onViewCourses: () -> Unit
) {
    var courseName by remember { mutableStateOf("") }
    var courseDuration by remember { mutableStateOf("") }
    var courseDescription by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = courseName,
            onValueChange = { courseName = it },
            label = { Text("Course Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textStyle = TextStyle(fontSize = 15.sp),
            singleLine = true
        )

        OutlinedTextField(
            value = courseDuration,
            onValueChange = { courseDuration = it },
            label = { Text("Course Duration") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textStyle = TextStyle(fontSize = 15.sp),
            singleLine = true
        )

        OutlinedTextField(
            value = courseDescription,
            onValueChange = { courseDescription = it },
            label = { Text("Course Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textStyle = TextStyle(fontSize = 15.sp),
            singleLine = true
        )

        Button(
            onClick = {
                when {
                    courseName.isBlank() -> Toast.makeText(context, "Please enter course name", Toast.LENGTH_SHORT).show()
                    courseDuration.isBlank() -> Toast.makeText(context, "Please enter course duration", Toast.LENGTH_SHORT).show()
                    courseDescription.isBlank() -> Toast.makeText(context, "Please enter course description", Toast.LENGTH_SHORT).show()
                    else -> viewModel.addCourse(
                        courseName = courseName,
                        courseDuration = courseDuration,
                        courseDescription = courseDescription,
                        context = context
                    ) {
                        // Clear fields on success
                        courseName = ""
                        courseDuration = ""
                        courseDescription = ""
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Add Course")
        }

        Button(
            onClick = onViewCourses,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("View Courses")
        }
    }
}