package projekt.cloud.piece.pic.api.comics.search

import kotlinx.serialization.Serializable

@Serializable
data class AdvancedSearchRequestBody(val categories: List<String>, val keyword: String, val sort: String)