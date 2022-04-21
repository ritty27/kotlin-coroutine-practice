package com.github.ritty.coroutine

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest.of
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.Thread.sleep
import java.time.Duration
import java.time.LocalDateTime
import javax.persistence.*
import javax.transaction.Transactional

@RestController
@RequestMapping("/api/event/v1/quiz")
class TestController(
  private val testService: TestService,
  private val testTableRepository: TestTableRepository
//    private val quizEventRepository: QuizEventRepository
) {
  private val log = LoggerFactory.getLogger(TestController::class.java)


  @GetMapping
  fun run() {
    val startTime = LocalDateTime.now()
    println("startTime: $startTime")

    val page = of(0, 10)
//        page.next()
    val list: Page<TestTable> = testTableRepository.findAll(page)
    println(list)
    testTableRepository.findById(1111L)
    testTableRepository.findById(2L)

//        list.forEach {
//            GlobalScope.launch {
//                testService.oneTask(it)
////            }
//            }
//
//        }
//        testService.useCoroutines()

    val endTme = LocalDateTime.now()
    println("startTime: $endTme")
    println("${Duration.between(startTime, endTme)}")


  }
}

@Service
open class TestService(
  private val testTableRepository: TestTableRepository
) {

  private val log = LoggerFactory.getLogger(TestService::class.java)

  fun nonCoroutinesOneTask(list: List<TestTable>) {
    for (one in list) {
      sleep(100L)
      one.test = "${one.id}수정 확인 중"
      log.info("${Thread.currentThread()}  ${one.id}")
    }
  }

  @Transactional
  open fun notCoroutines(
  ) {
    val allList = testTableRepository.findAll()
    nonCoroutinesOneTask(allList)
  }

  //    suspend
  @Transactional
  open fun oneTask(list: List<TestTable>) {
    for (one in list) {
      if (one.id == 201L)
        throw java.lang.RuntimeException()
      one.test = "찐막 확인하는 중${one.id}"

    }
  }

  @Transactional
  open fun useCoroutines() {
    val list: List<List<TestTable>> = testTableRepository.findAll().chunked(10)

    list.forEach {
//            GlobalScope.launch {
//            oneTask(it)
      for (one in it) {
        if (one.id == 101L)
          throw java.lang.RuntimeException()
        one.test = "찐막 확인하는 중${one.id}"

      }
//            }
    }
  }
}

@Entity
@Table(name = "test_table")
class TestTable(

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "int")
  val id: Long? = null,

  var test: String

)


interface TestTableRepository : JpaRepository<TestTable, Long> {
}

@Entity
@Table(name = "log_table")
class LogTable(

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "int")
  val id: Long? = null,

  val thread: String

)


interface LogTableRepository : JpaRepository<LogTable, Long> {
}