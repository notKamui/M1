#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <iostream>


///////////////////////////////////////////////////////////////////////////////
cv::Mat computeHistogramGS(const cv::Mat &image)
{
  // create and initialize and 1d array of integers
  cv::Mat histogram(256,1,CV_32S,cv::Scalar(0));

  // compute the histogram
  for(int i=0; i<image.rows; ++i)
    for(int j=0; j<image.cols; ++j)
      histogram.at<int>(image.at<unsigned char>(i,j))++;

  return histogram;
}

//////////////////////////////////////////////////////////////////////////////////////////////////
cv::Mat histogramToImageGS(const cv::Mat &histogram)
{
  // create an image
  const unsigned int histoHeight = 100;
  cv::Mat histogramImage(histoHeight,256,CV_8U,cv::Scalar(0));

  // find the max value of the histogram
  double minValue,maxValue;
  cv::minMaxLoc(histogram, &minValue, &maxValue);

  // write the histogram lines
  for(int j=0; j<256; ++j)
    cv::line(histogramImage, cv::Point(j,histoHeight), cv::Point(j,histoHeight-(histoHeight*histogram.at<int>(j))/maxValue), cv::Scalar(255), 1);

  return histogramImage;
}

//////////////////////////////////////////////////////////////////////////////////////////////////
int main(int argc, char ** argv)
{
  // check arguments
  if(argc != 2){
    std::cout << "usage: " << argv[0] << " image" << std::endl;
    return -1;
  }

  // load the input image
  std::cout << "load image ..." << std::endl;
  cv::Mat image = cv::imread(argv[1]);
  if(image.empty()){
    std::cout << "error loading " << argv[1] << std::endl;
    return -1;
  }
  std::cout << "image size : " << image.cols << " x " << image.rows << std::endl;

  // display an image
  std::cout << "appuyer sur une touche ..." << std::endl;
  cv::imshow("image", image);
  cv::waitKey();

  // convert the image to grayscale
  // cvtColor(image,image,CV_BGR2GRAY);
  cvtColor(image,image,cv::COLOR_BGR2GRAY);


  // display an image
  std::cout << "appuyer sur une touche ..." << std::endl;
  cv::imshow("image", image);
  cv::imshow("histogramme", histogramToImageGS(computeHistogramGS(image)));
  cv::waitKey();

  // save images
  cv::imwrite("output/imageGS.jpg",image);
  cv::imwrite("output/histogram.jpg",histogramToImageGS(computeHistogramGS(image)));


  return 1;
}
