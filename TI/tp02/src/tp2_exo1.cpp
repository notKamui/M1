#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <iostream>

//////////////////////////////////////////////////////////////////////////////////////////////////

cv::Mat extrapolate(const cv::Mat& base)
{
  cv::Mat extra(base.rows * 3, base.cols * 3, CV_8UC3, cv::Scalar(0, 0, 0));
  
  for (int j = 0; j < extra.cols; j++)
  {
    for (int i = 0; i < extra.rows; i++)
    {
      int ti = i % base.rows;
      if (i % (base.rows*2) < base.rows)
      {
        ti = base.rows - ti;
      }
      int tj = j % base.cols;
      if (j % (base.cols*2) < base.cols)
      {
        tj = base.cols - tj;
      }
      extra.at<cv::Vec3b>(i, j) = base.at<cv::Vec3b>(ti, tj);
    }
  }

  return extra;
}

cv::Mat rotateLeftFrom(const cv::Mat& image, int x, int y)
{
  cv::Point2d center(x, y);
  cv::Mat rotated = image;
  cv::Mat rotation = cv::getRotationMatrix2D(center, 10, 1);
  for (int i = 0; i < 20; i++)
  {
    cv::warpAffine(rotated, rotated, rotation, image.size());
  }
  return rotated;
}

void callBackKeyboard(int event, int x, int y, int flags, void *userdata)
{
  switch (event)
  {
  case cv::EVENT_LBUTTONDOWN:
    std::cout << "left button pressed at : " << x << ", " << y << std::endl;
    cv::Mat image = *((cv::Mat*)&userdata);
    rotateLeftFrom(image, x, y);
    break;

  case cv::EVENT_RBUTTONDOWN:
  case cv::EVENT_MBUTTONDOWN:
  case cv::EVENT_MOUSEMOVE:
    break;
  }
}

//////////////////////////////////////////////////////////////////////////////////////////////////
int main(int argc, char **argv)
{
  // check arguments
  if (argc != 2)
  {
    std::cout << "usage: " << argv[0] << " image" << std::endl;
    return -1;
  }

  // load the input image
  std::cout << "load image ..." << std::endl;
  cv::Mat image = cv::imread(argv[1]);
  if (image.empty())
  {
    std::cout << "error loading " << argv[1] << std::endl;
    return -1;
  }
  std::cout << "image size : " << image.cols << " x " << image.rows << std::endl;

  // setup a window
  cv::namedWindow("image", 1);
  cv::setMouseCallback("image", callBackKeyboard, image);

  cv::Mat extrapolated = extrapolate(image);
  cv::imwrite("output/extrapolated.jpg", extrapolated);

  // main loop
  bool loopOn = true;
  while (loopOn)
  {

    // display the image
    cv::imshow("image", image);

    // if esc button is pressed
    int key = cv::waitKey(500) % 256;
    if (key == 27 || key == 'q')
      loopOn = false;
  }

  return 1;
}
