/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

@file:UseSerializers(AnySerializer::class)

package org.eclipse.tractusx.managedidentitywallets.models.ssi

import com.fasterxml.jackson.annotation.JsonProperty
import io.bkbn.kompendium.annotations.Field
import io.bkbn.kompendium.annotations.Param
import io.bkbn.kompendium.annotations.ParamType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.eclipse.tractusx.managedidentitywallets.plugins.AnySerializer

@Serializable
data class VerifiablePresentationDto(
    @SerialName("@context") @JsonProperty("@context")
    @Field(description = "List of Contexts", name = "@context")
    val context: List<String>,
    @Field(description = "The ID of Credential as String (URI compatible)", name = "id")
    val id: String? = null,
    @Field(description = "List of Types", name = "type")
    val type: List<String>,
    @Field(description = "The DID of the Holder as String (URI compatible)", name = "holder")
    val holder: String? = null,
    @Field(description = "List of Verifiable Credentials", name = "verifiableCredential")
    val verifiableCredential: List<VerifiableCredentialDto>? = null,
    @Field(description = "The Proof generated by the holder", name = "proof")
    val proof: LdProofDto? = null
)

@Serializable
data class VerifiablePresentationRequestDto(
    @Field(description = "The DID or BPN of the Holder", name = "holderIdentifier")
    val holderIdentifier: String,
    @Field(description = "List of Verifiable Credentials", name = "verifiableCredentials")
    val verifiableCredentials: List<VerifiableCredentialDto>
)

@Serializable
data class WithDateValidation(
    @Param(type = ParamType.QUERY)
    @Field(
        description = "Flag whether issuance and expiration date of all credentials should be validated",
        name = "withDateValidation"
    )
    val withDateValidation: Boolean? = false,
    @Param(type = ParamType.QUERY)
    @Field(
        description = "Optional flag to check if any of the verifiable credentials has been revoked. Default is true",
        name = "withRevocationValidation"
    )
    val withRevocationValidation: Boolean = true
)

@Serializable
data class VerifiablePresentationIssuanceParameter(
    @Param(type = ParamType.QUERY)
    @Field(
        description = "Optional Flag whether to check if the Verifiable Credentials are valid. Default is true",
        name = "withCredentialsValidation"
    )
    val withCredentialsValidation: Boolean = true,
    @Param(type = ParamType.QUERY)
    @Field(
        description = "Optional Flag whether to check if the Dates of the Verifiable Credentials are valid. " +
                "Default is true. If `withCredentialsValidation` is false then this value will be ignored.",
        name = "withCredentialsDateValidation"
    )
    val withCredentialsDateValidation: Boolean = true,
    @Param(type = ParamType.QUERY)
    @Field(
        description = "Optional flag to check if any of the verifiable credentials has been revoked. Default is true",
        name = "withRevocationValidation"
    )
    val withRevocationValidation: Boolean = true
)
