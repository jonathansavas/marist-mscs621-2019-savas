package com.github.jonathansavas.parabond.paraworker.scala

import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.{GrpcPartition, GrpcResult}
import org.apache.logging.log4j.LogManager
import parabond.cluster.{FineGrainedNode, Partition}

/**
  * Worker class to analyze a partition of bond portfolios. Queries a
  * Mongo DB for portfolio and bond information.
  * VM Options:
  *   -Dhost=[mongo_ip_address], defaults to localhost
  * @author Jonathan Savas
  */
class ParaWorker {
  private val logger = LogManager.getLogger(classOf[ParaWorker])

  logger.info("ParaWorker created")

  val node = new FineGrainedNode

  def work(grpcPartition: GrpcPartition): GrpcResult = {
    val partition = Partition(grpcPartition.getN, grpcPartition.getBegin)

    logger.info("Analyzing partition {}", partition)

    val analysis = node.analyze(partition)

    analysis.results.foreach { jo =>
      logger.debug("DEBUG PORTFID {} PARAWORKER", jo.portfId)
    }

    logger.info("Received analysis for partition {}", partition)

    val partialT1 = analysis.results.foldLeft(0L) { (sum, job) =>
      val time = job.result.t1 - job.result.t0

      sum + time
    }

    GrpcResult.newBuilder().setT1(partialT1).setT0(0).build();
  }
}
