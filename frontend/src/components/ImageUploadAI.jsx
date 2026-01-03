import React, { useState, useRef } from 'react';
import { analyzeMaintenanceImage } from '../utils/api';

const ImageUploadAI = ({ onDescriptionGenerated }) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [isDragging, setIsDragging] = useState(false);
  const [success, setSuccess] = useState(false);
  const fileInputRef = useRef(null);

  const ALLOWED_TYPES = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'];
  const MAX_SIZE = 10 * 1024 * 1024; // 10MB

  const formatFileSize = (bytes) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  };

  const validateFile = (file) => {
    if (!file) {
      return 'No file selected';
    }
    if (!ALLOWED_TYPES.includes(file.type)) {
      return 'Invalid file type. Please upload JPG, PNG, or WEBP images only.';
    }
    if (file.size > MAX_SIZE) {
      return 'File size exceeds 10MB limit. Please choose a smaller image.';
    }
    return null;
  };

  const handleFileSelect = (file) => {
    setError(null);
    setSuccess(false);

    const validationError = validateFile(file);
    if (validationError) {
      setError(validationError);
      triggerErrorAnimation();
      return;
    }

    setSelectedFile(file);
    const reader = new FileReader();
    reader.onloadend = () => {
      setPreview(reader.result);
    };
    reader.readAsDataURL(file);
  };

  const handleDragEnter = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragging(true);
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragging(false);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    e.stopPropagation();
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragging(false);

    const file = e.dataTransfer.files[0];
    if (file) {
      handleFileSelect(file);
    }
  };

  const handleFileInputChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      handleFileSelect(file);
    }
  };

  const handleRemoveImage = () => {
    setSelectedFile(null);
    setPreview(null);
    setError(null);
    setSuccess(false);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const triggerErrorAnimation = () => {
    const container = document.getElementById('upload-container');
    if (container) {
      container.classList.add('shake-animation');
      setTimeout(() => {
        container.classList.remove('shake-animation');
      }, 500);
    }
  };

  const handleGenerateDescription = async () => {
    if (!selectedFile) return;

    setIsLoading(true);
    setError(null);
    setSuccess(false);

    try {
      const result = await analyzeMaintenanceImage(selectedFile);

      if (result.success && result.description) {
        setSuccess(true);
        onDescriptionGenerated(result.description);

        // Scroll to description field smoothly and auto-resize
        setTimeout(() => {
          const descriptionField = document.querySelector('textarea[name="description"]');
          if (descriptionField) {
            // Auto-resize the textarea based on content
            descriptionField.style.height = 'auto';
            descriptionField.style.height = descriptionField.scrollHeight + 'px';

            descriptionField.scrollIntoView({ behavior: 'smooth', block: 'center' });
            descriptionField.focus();
          }
        }, 300);
      } else {
        setError(result.error || 'Failed to analyze image. Please try again.');
        triggerErrorAnimation();
      }
    } catch (err) {
      console.error('Error analyzing image:', err);
      setError(err.response?.data?.error || 'Network error. Please check your connection and try again.');
      triggerErrorAnimation();
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="mb-6">
      <style>{`
        @keyframes shake {
          0%, 100% { transform: translateX(0); }
          25% { transform: translateX(-10px); }
          75% { transform: translateX(10px); }
        }

        @keyframes fadeInUp {
          from {
            opacity: 0;
            transform: translateY(20px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }

        @keyframes pulse {
          0%, 100% { opacity: 1; }
          50% { opacity: 0.5; }
          }

        @keyframes shimmer {
          0% { background-position: -1000px 0; }
          100% { background-position: 1000px 0; }
        }

        @keyframes spin {
          from { transform: rotate(0deg); }
          to { transform: rotate(360deg); }
        }

        @keyframes checkmark {
          0% {
            stroke-dashoffset: 100;
          }
          100% {
            stroke-dashoffset: 0;
          }
        }

        @keyframes typing {
          0%, 100% { opacity: 0; }
          50% { opacity: 1; }
        }

        .shake-animation {
          animation: shake 0.5s;
        }

        .fade-in-up {
          animation: fadeInUp 0.5s ease-out;
        }

        .pulse-animation {
          animation: pulse 2s ease-in-out infinite;
        }

        .shimmer-button {
          background: linear-gradient(
            90deg,
            #7C3AED 0%,
            #8B5CF6 25%,
            #A78BFA 50%,
            #8B5CF6 75%,
            #7C3AED 100%
          );
          background-size: 200% auto;
          animation: shimmer 3s linear infinite;
        }

        .spinner {
          animation: spin 1s linear infinite;
        }

        .checkmark-path {
          stroke-dasharray: 100;
          stroke-dashoffset: 100;
          animation: checkmark 0.5s ease-out forwards;
        }

        .typing-dots::after {
          content: '...';
          animation: typing 1.5s infinite;
        }

        .drag-border-pulse {
          animation: pulse 1.5s ease-in-out infinite;
        }
      `}</style>

      <label className="block text-sm font-semibold text-gray-700 mb-3">
        Upload Maintenance Issue Image
      </label>

      <div id="upload-container" className="transition-all duration-300">
        {!preview ? (
          <div
            className={`
              relative border-2 border-dashed rounded-2xl p-8 text-center cursor-pointer
              transition-all duration-300 bg-white/80 backdrop-blur-sm
              ${isDragging
                ? 'border-violet-500 bg-violet-50/50 scale-105 shadow-lg shadow-violet-500/30 drag-border-pulse'
                : 'border-violet-300 hover:border-violet-400 hover:bg-violet-50/30 hover:scale-102 hover:shadow-lg hover:shadow-violet-500/20'
              }
              ${error ? 'border-red-400 bg-red-50/30' : ''}
            `}
            onDragEnter={handleDragEnter}
            onDragLeave={handleDragLeave}
            onDragOver={handleDragOver}
            onDrop={handleDrop}
            onClick={() => fileInputRef.current?.click()}
          >
            <input
              ref={fileInputRef}
              type="file"
              accept="image/jpeg,image/jpg,image/png,image/webp"
              onChange={handleFileInputChange}
              className="hidden"
            />

            <div className={`transition-transform duration-300 ${isDragging ? 'scale-110' : ''}`}>
              <svg
                className="mx-auto h-16 w-16 text-violet-400 mb-4"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1.5}
                  d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
                />
              </svg>

              <p className="text-lg font-medium text-gray-700 mb-2">
                {isDragging ? 'Drop image here' : 'Drop maintenance issue image here'}
              </p>
              <p className="text-sm text-gray-500 mb-4">
                or click to browse
              </p>

              <div className="flex justify-center gap-2 mb-4">
                <span className="px-3 py-1 text-xs font-medium bg-violet-100 text-violet-700 rounded-full">
                  JPG
                </span>
                <span className="px-3 py-1 text-xs font-medium bg-violet-100 text-violet-700 rounded-full">
                  PNG
                </span>
                <span className="px-3 py-1 text-xs font-medium bg-violet-100 text-violet-700 rounded-full">
                  WEBP
                </span>
              </div>

              <p className="text-xs text-gray-400">
                Max size: 10MB
              </p>
            </div>
          </div>
        ) : (
          <div className="bg-white/80 backdrop-blur-sm border-2 border-violet-300 rounded-2xl p-6 shadow-lg shadow-violet-500/20 fade-in-up">
            <div className="flex items-start gap-4 mb-4">
              <div className="relative flex-shrink-0">
                <img
                  src={preview}
                  alt="Preview"
                  className="w-32 h-32 object-cover rounded-xl shadow-md"
                />
                <button
                  onClick={handleRemoveImage}
                  className="absolute -top-2 -right-2 bg-red-500 text-white rounded-full p-1.5 hover:bg-red-600 transition-all duration-200 hover:scale-110 shadow-lg"
                  disabled={isLoading}
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
                {success && (
                  <div className="absolute -bottom-2 -right-2 bg-green-500 text-white rounded-full p-1 shadow-lg">
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path
                        className="checkmark-path"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={3}
                        d="M5 13l4 4L19 7"
                      />
                    </svg>
                  </div>
                )}
              </div>

              <div className="flex-1 min-w-0">
                <p className="text-sm font-semibold text-gray-800 truncate mb-1">
                  {selectedFile?.name}
                </p>
                <p className="text-xs text-gray-500 mb-2">
                  {formatFileSize(selectedFile?.size || 0)}
                </p>
                {success && (
                  <span className="inline-flex items-center px-2.5 py-1 text-xs font-medium bg-green-100 text-green-700 rounded-full">
                    <svg className="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                    </svg>
                    AI Generated
                  </span>
                )}
              </div>
            </div>

            <button
              onClick={handleGenerateDescription}
              disabled={isLoading}
              className={`
                w-full py-3 px-6 rounded-xl font-semibold text-white
                transition-all duration-300 transform
                ${isLoading
                  ? 'bg-gray-400 cursor-not-allowed'
                  : 'shimmer-button hover:scale-105 hover:shadow-xl hover:shadow-violet-500/40 active:scale-95'
                }
                flex items-center justify-center gap-2
              `}
            >
              {isLoading ? (
                <>
                  <svg className="w-5 h-5 spinner" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                  <span className="typing-dots">AI is analyzing your image</span>
                </>
              ) : (
                <>
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
                  </svg>
                  Generate Description with AI
                </>
              )}
            </button>
          </div>
        )}

        {error && (
          <div className="mt-4 p-4 bg-red-50 border border-red-200 rounded-xl flex items-start gap-3 fade-in-up">
            <svg className="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
            </svg>
            <div className="flex-1">
              <p className="text-sm font-medium text-red-800">{error}</p>
              {selectedFile && (
                <button
                  onClick={() => setError(null)}
                  className="text-xs text-red-600 hover:text-red-700 mt-1 underline"
                >
                  Dismiss
                </button>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ImageUploadAI;
