import React from 'react';

interface LoaderProps {
  size?: 'sm' | 'md' | 'lg';
  className?: string;
  isFullPage?: boolean;
}

const Loader: React.FC<LoaderProps> = ({ 
  size = 'md', 
  className = '', 
  isFullPage = false 
}) => {
  const sizeClasses = {
    sm: 'w-6 h-6 border-2',
    md: 'w-8 h-8 border-4',
    lg: 'w-12 h-12 border-4'
  };

  const loader = (
    <div className={`border-primary border-t-transparent rounded-full animate-spin ${sizeClasses[size]} ${className}`} />
  );

  if (isFullPage) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        {loader}
      </div>
    );
  }

  return loader;
};

export default Loader;
