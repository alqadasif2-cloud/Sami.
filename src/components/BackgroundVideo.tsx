import React, { useRef, useEffect } from 'react';

interface BackgroundVideoProps {
  overlayOpacity?: string; // e.g. "bg-black/65"
  className?: string;
}

export const BackgroundVideo: React.FC<BackgroundVideoProps> = ({
  overlayOpacity = 'bg-black/65',
  className = '',
}) => {
  const videoRef = useRef<HTMLVideoElement>(null);

  useEffect(() => {
    const video = videoRef.current;
    if (video) {
      video.muted = true;
      video.defaultMuted = true;
      video.playsInline = true;
      const playPromise = video.play();
      if (playPromise !== undefined) {
        playPromise.catch(() => {
          // Autoplay was prevented, will play on user interaction
        });
      }
    }
  }, []);

  return (
    <div
      className={`fixed inset-0 w-full h-full overflow-hidden pointer-events-none z-0 ${className}`}
      aria-hidden="true"
    >
      <video
        ref={videoRef}
        src="/background_video.mp4"
        autoPlay
        loop
        muted
        playsInline
        preload="auto"
        className="w-full h-full object-cover object-center transform scale-105 select-none"
      />
      {/* 60-70% Dark Overlay for crystal clear typography and accessibility */}
      <div className={`absolute inset-0 ${overlayOpacity} backdrop-blur-[1px]`} />
      <div className="absolute inset-0 bg-gradient-to-b from-[#070a10]/40 via-transparent to-[#070a10]/80" />
    </div>
  );
};
