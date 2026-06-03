# Feedback Service

Handles text, audio, video, and image feedback with MinIO pre-signed URL uploads.

## Endpoints
- POST /api/v1/feedbacks/text — Submit text feedback
- GET /api/v1/feedbacks/upload-url — Get pre-signed URL for media upload
- POST /api/v1/feedbacks/media — Submit media feedback (after upload)
- GET /api/v1/feedbacks/my — User's feedback history
