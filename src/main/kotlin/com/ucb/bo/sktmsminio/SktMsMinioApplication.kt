package com.ucb.bo.sktmsminio

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class SktMsMinioApplication

fun main(args: Array<String>) {
	runApplication<SktMsMinioApplication>(*args)
}
