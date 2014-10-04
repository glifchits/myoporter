/*
AAD-only 
*/

#ifndef NCL_AAD_H_INCLUDED
#define NCL_AAD_H_INCLUDED

#include "ncl.h"

#ifdef __cplusplus
extern "C" {
#endif

#define MAX_NAME_SIZE 18

typedef enum{
	NCL_EVENT_ANY_AAD,
	NCL_EVENT_AUTHENTICATION,
	NCL_EVENT_UPDATE_FIRMWARE_PROGRESS,
	NCL_EVENT_UPDATED_FIRMWARE,
	NCL_EVENT_NEAS,
	NCL_EVENT_NEA,
	NCL_EVENT_REVOCATION,
	NCL_EVENT_BIOTEMPLATE_KEY,
	NCL_EVENT_STATUS
} NclAadEventType;

typedef enum{
	NCL_STATUS_UNAUTHENTICATED,
	NCL_STATUS_AUTHENTICATED,
	NCL_STATUS_DEV
} NclStatus;

typedef struct{
	NclProvisionId id;
	char name[MAX_NAME_SIZE+1];
} NclNea;

typedef struct{
	NclAadEventType type;
	union{
		//generic completion fields
		struct{
			int nymiHandle;
		} completion;
		//update firmware progress fields
		struct{
			int nymiHandle;
			NclBool sendingBle;//NCL_TRUE if sending BLE image, NCL_FALSE if sending MCU image
			unsigned index;//how many bytes of the current image have been sent
		} updateFirmwareProgress;
		//NEAs fields
		struct{
			int nymiHandle;
			unsigned neas;
		} neas;
		//NEA fields
		struct{
			int nymiHandle;
			NclNea nea;
		} nea;
		//Biotemplate key fields
		struct{
			int nymiHandle;
			NclSk key;
		} biotemplateKey;
		//status fields
		struct{
			int nymiHandle;
			NclStatus status;
		} status;
	};
} NclAadEvent;

typedef void (*NclAadCallback)(NclAadEvent, void* userData);

//these are analogous to nclAddBehavior and nclRemoveBehavior
NCL NclBool nclAddAadBehavior(NclAadCallback callback, void* userData, NclAadEventType eventType, int nymiHandle);
NCL NclBool nclRemoveAadBehavior(NclAadCallback callback, void* userData, NclAadEventType eventType, int nymiHandle);

/*! \brief Discover Nymis in the specified enrollment state.

By default, enrolled Nymis are discovered.

\pre NCL is initialized.
\post Only Nymis in the specified enrollment state are discovered.

\param [in] enrolled NCL_TRUE if you want to discover enrolled Nymis, NCL_FALSE if you want to discover unenrolled Nymis.

\return  \ref NCL_TRUE On success.
\return  \ref NCL_FALSE On failure.
*/
NCL NclBool nclSetDiscovery(NclBool enrolled);

/*! \brief Authenticate a Nymi.

\pre NCL is initialized and an AAD callback is set. The specified Nymi is validated and has a free command channel.
\post The Nymi's command channel is taken. An NCL_EVENT_AUTHENTICATION will be triggered when the Nymi is authenticated, at which point the command channel is free.

\param [in] nymiHandle The handle of the Nymi you want to authenticate.
\param [in] sk The Nymi state encryption key. Pass zeroes for failed authentication.

\return NCL_TRUE On successful initiation.
\return NCL_FALSE On failed initiation.
*/
NCL NclBool nclAuthenticate(int nymiHandle, NclSk sk);

/*! \brief Update a Nymi's firmware.

\pre NCL is initialized and an AAD callback is set. The specified Nymi is validated and has a free command channel.
\post The Nymi's command channel is taken. NCL_EVENT_UPDATE_FIRMWARE_PROGRESS will be triggered the number of times specified as the firmware is updated. NCL_EVENT_UPDATED_FIRMWARE will be triggered upon completion, at which point the command channel is free.

\param [in] nymiHandle The handle of the Nymi whose firmware you want to update.
\param [in] bleImage The BLE firmware image.
\param [in] bleSize The size of the BLE firmware image.
\param [in] bleAddress The address the Nymi should write the BLE firmware image at.
\param [in] mcuImage The MCU firmware image.
\param [in] mcuSize The size of the MCU firmware image.
\param [in] mcuAddress The address the Nymi should write the MCU firmware image at.
\param [in] progressCallbacks The number of NCL_EVENT_UPDATE_FIRMWARE_PROGRESS to trigger over the course of the upload.

\return NCL_TRUE On successful initiation.
\return NCL_FALSE On failed initiation.
*/
NCL NclBool nclUpdateFirmware(
	int nymiHandle,
	const NclUInt8* bleImage, unsigned bleSize, unsigned bleAddress,
	const NclUInt8* mcuImage, unsigned mcuSize, unsigned mcuAddress,
	unsigned progressCallbacks
);

/*! \brief Get the number of NEAs on a Nymi.

\pre NCL is initialized and an AAD callback is set. The specified Nymi is validated and has a free command channel.
\post The Nymi's command channel is taken. An NCL_EVENT_NEAS event will be triggered, at which point the command channel is free.

\param [in] nymiHandle The handle of the Nymi you want to get the number of NEAs on.

\return NCL_TRUE On successful initiation.
\return NCL_FALSE On failed initiation.
*/
NCL NclBool nclNeas(int nymiHandle);

/*! \brief Get an NEA from a Nymi.

\pre NCL is initialized and an AAD callback is set. The specified Nymi is validated and has a free command channel.
\post The Nymi's command channel is taken. An NCL_EVENT_NEA event will be triggered, at which point the command channel is free.

\param [in] nymiHandle The handle of the Nymi you want to get the number of NEAs on.
\param [in] nea The index of the NEA you want to get. If it is not in the range [0, n) where n is the number of NEAs on the nymi, an error will be returned.

\return NCL_TRUE On successful initiation.
\return NCL_FALSE On failed initiation.
*/
NCL NclBool nclNea(int nymiHandle, unsigned char nea);

/*! \brief Revoke a Nymi's NEA.

\pre NCL is initialized and an AAD callback is set. The specified Nymi is validated and has a free command channel.
\post The Nymi's command channel is taken. An NCL_EVENT_REVOCATION will be triggered when the NEA is revoked, at which point the command channel is free.

\param [in] nymiHandle The handle of the Nymi you want to revoke an NEA on.
\param [in] nea The provision ID of the NEA you want to revoke.

\return NCL_TRUE On successful initiation.
\return NCL_FALSE On failed initiation.
*/
NCL NclBool nclRevokeNea(int nymiHandle, const NclProvisionId nea);

/*! \brief Clear a Nymi's flash.

\pre NCL is initialized and an AAD callback is set. The specified Nymi is validated and has a free command channel.
\post The Nymi's command channel is taken. The Nymi should then disconnect as the validation is no longer valid.

\param [in] nymiHandle The handle of the Nymi you want to clear.

\return NCL_TRUE On successful initiation.
\return NCL_FALSE On failed initiation.
*/
NCL NclBool nclClearProvisions(int nymiHandle);

/*! \brief Get the biotemplate key from a Nymi.

\pre NCL is initialized and an AAD callback is set. The specified Nymi is validated and has a free command channel.
\post The Nymi's command channel is taken. An NCL_EVENT_BIOTEMPLATE_KEY will be triggered, at which point the command channel is free.

\param [in] nymiHandle The handle of the Nymi whose biotemplate key you want.

\return NCL_TRUE On successful initiation.
\return NCL_FALSE On failed initiation.
*/
NCL NclBool nclGetBiotemplateKey(int nymiHandle);

/*! \brief Get the status of a Nymi.

\pre NCL is initialized. The specified Nymi is validated and has a free command channel.
\post The Nymi's command channel is taken. An NCL_EVENT_STATUS will be triggered, at which point the command channel is free.

\param [in] nymiHandle The handle of the Nymi you want to get the status of.

\return  \ref NCL_TRUE On successful initiation.
\return  \ref NCL_FALSE On failed initiation.
*/
NCL NclBool nclGetStatus(int nymiHandle);

#ifdef __cplusplus
}
#endif

#endif
